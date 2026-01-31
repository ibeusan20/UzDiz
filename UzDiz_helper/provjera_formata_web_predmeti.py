# Provjera pravila kodiranja za Java kod - WebKolegiji2025/2026
# -----------------------------------------------------------------
# Programski kod (klase, atributi i metode klasa, varijable, konstante,
# komentari i sl.) pišu se na hrvatskom jeziku. Metode u klasama NE smiju 
# imati više od 30 linija programskog koda, u što se ne broji definiranje 
# metode, njenih argumenata i lokalnih varijabli. U jednoj liniji može biti 
# jedna instrukcija. Linija ne može imati više od 120 znakova. Ne broji se 
# linija u kojoj je samo vitičasta zagrada ili je prazna linija. Ne broje 
# se linije u kojima se nalazi komentar osim u slučaju da se u komentaru 
# nalazi programski kod. Prekoračenja spomenutih ograničenja donose 
# penalizacijske bodove.

import os
import re

# ===== PODESAVANJE =====
CONTEXT = 2  # koliko linija prije/poslije ispisati kao kontekst

PENALTY_LONG_LINE = 1
PENALTY_MULTI_INSTR = 1
PENALTY_METHOD_TOO_LONG = 1
# ======================

CONTROL_START = re.compile(r'^\s*(if|for|while|switch|catch|do|try|else|case|default)\b')

# lokalna varijabla (ne broji se u limit od 30 linija koda)
LOCAL_VAR_DECL = re.compile(
    r'^\s*(?:final\s+)?(?:var|[\w\<\>\[\],\s\?]+)\s+[\w$]+\s*(?:=\s*.+)?;\s*$'
)

# početak metode/konstruktora u jednoj liniji
METHOD_START = re.compile(
    r'^\s*(?:@\w+(?:\([^)]*\))?\s*)*' # anotacije
    r'(?:public|private|protected)?\s*'
    r'(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?(?:abstract\s+)?'
    r'(?:[\w\<\>\[\]]+\s+)?'# povratni tip (može faliti za konstruktor)
    r'[\w$]+\s*\([^;]*\)\s*(?:throws\s+[\w$.,\s]+)?\s*\{?\s*$'
)

def strip_strings(s: str) -> str:
    # Makne sadržaj stringova/charova (da ; i {} u stringu ne smetaju).
    out = []
    i = 0
    n = len(s)
    while i < n:
        c = s[i]
        if c == '"':
            out.append('"')
            i += 1
            while i < n:
                if s[i] == '\\':
                    i += 2
                elif s[i] == '"':
                    i += 1
                    break
                else:
                    i += 1
            out.append('"')
        elif c == "'":
            out.append("'")
            i += 1
            while i < n:
                if s[i] == '\\':
                    i += 2
                elif s[i] == "'":
                    i += 1
                    break
                else:
                    i += 1
            out.append("'")
        else:
            out.append(c)
            i += 1
    return ''.join(out)

def remove_line_comment(code: str) -> str:
    idx = code.find("//")
    return code if idx == -1 else code[:idx]

def process_comments(line: str, in_block: bool):
    # Vrati (code_part, in_block_comment).
    # Uklanja /* */ sadržaj, i // dio (ali // nakon koda odstrani).
    s = line
    code = ""
    i = 0
    while i < len(s):
        if in_block:
            end = s.find("*/", i)
            if end == -1:
                return code, True
            i = end + 2
            in_block = False
        else:
            start = s.find("/*", i)
            sl = s.find("//", i)
            if sl != -1 and (start == -1 or sl < start):
                code += s[i:sl]
                return code, False
            if start == -1:
                code += s[i:]
                return code, False
            code += s[i:start]
            i = start + 2
            in_block = True
    return code, in_block

def count_semicolons_safely(code_no_comments: str) -> int:
    # Broji ; izvan stringova, i pokušava ignorirati klasični for(...;...;...).
    c = strip_strings(code_no_comments)
    # ignorira semikolone unutar for( ... )
    m = re.search(r'\bfor\s*\((.*)\)', c)
    if m:
        inside = m.group(1)
        before = c[:m.start(1)]
        after = c[m.end(1):]
        # makni ; iz inside
        inside = inside.replace(";", "")
        c = before + inside + after
    return c.count(";")

def braces_delta(code_no_comments: str) -> int:
    c = strip_strings(code_no_comments)
    return c.count("{") - c.count("}")

def is_only_brace_or_empty(code: str) -> bool:
    t = code.strip()
    return t == "" or t == "{" or t == "}"

def looks_like_comment_only(raw_line: str, code_part: str) -> bool:
    # ako nakon skidanja komentara nema koda, a original ima comment marker
    raw = raw_line.strip()
    return code_part.strip() == "" and (raw.startswith("//") or raw.startswith("/*") or raw.startswith("*"))

def comment_contains_codeish(raw_line: str) -> bool:
    # po pravilima: komentar se ne broji osim ako sadrži kod
    # ako u komentaru ima ; { } (ili npr. "if("), tretiraj kao kod
    t = raw_line.strip()
    return (";" in t) or ("{" in t) or ("}" in t) or ("if(" in t) or ("for(" in t) or ("while(" in t)

def is_local_var_line(code: str) -> bool:
    t = code.strip()
    if t == "":
        return False
    if CONTROL_START.match(t):
        return False
    if t.startswith(("return", "throw", "break", "continue", "yield", "new ")):
        return False
    if "(" in t:  # poziv metode/lamda itd. -> nije čista deklaracija varijable (heuristika)
        return False
    return bool(LOCAL_VAR_DECL.match(t))

def snippet(lines, lineno, context=CONTEXT):
    start = max(1, lineno - context)
    end = min(len(lines), lineno + context)
    out = []
    for ln in range(start, end + 1):
        prefix = ">>" if ln == lineno else "  "
        out.append(f"{prefix} {ln:4d}: {lines[ln-1].rstrip()}")
    return "\n".join(out)

def provjeri_datoteku(putanja):
    with open(putanja, encoding="utf-8", errors="ignore") as f:
        lines = f.readlines()

    preduge_linije = [] # (lineno, length)
    viselinijske_instr = [] # (lineno, semicolons)
    metode = [] # (start_line, signature, code_lines_count, first_excess_line)

    in_block = False

    in_method = False
    method_start_line = 0
    method_sig = ""
    brace_depth = 0
    pending_open_brace = False

    code_lines_count = 0
    first_excess_line = None

    for i, raw in enumerate(lines, start=1):
        # provjera duljine linije (raw bez \n)
        raw_len = len(raw.rstrip("\n"))
        if raw_len > 120:
            preduge_linije.append((i, raw_len))

        # miče komentare + stringove (za dalje)
        code_part, in_block = process_comments(raw, in_block)
        code_part = code_part.rstrip("\n")
        code_no_comments = code_part

        # više instrukcija u jednoj liniji (heuristika)
        semis = count_semicolons_safely(code_no_comments)
        if semis > 1:
            viselinijske_instr.append((i, semis))

        # detekcija početka metode (samo ako nismo već unutra)
        stripped = code_no_comments.strip()

        if not in_method:
            if stripped and not CONTROL_START.match(stripped) and METHOD_START.match(stripped):
                in_method = True
                method_start_line = i
                method_sig = stripped
                code_lines_count = 0
                first_excess_line = None

                delta = braces_delta(code_no_comments)
                brace_depth = delta
                pending_open_brace = (brace_depth <= 0 and "{" not in code_no_comments)
                # ne broji liniju potpisa metode
                continue

        # praćenje tijela metode
        if in_method:
            # čekam da se otvori { ako potpis nije imao {
            if pending_open_brace:
                if "{" in code_no_comments:
                    pending_open_brace = False
                    brace_depth += braces_delta(code_no_comments)
                # ne broji dok ne uđe u tijelo
                continue

            # broji "stvarne" linije u metodi po pravilima (heuristika)
            if not is_only_brace_or_empty(code_no_comments):
                if looks_like_comment_only(raw, code_no_comments):
                    # komentar linija: broji samo ako izgleda kao kod
                    if comment_contains_codeish(raw):
                        code_lines_count += 1
                else:
                    # nije komentar-only
                    if not is_local_var_line(code_no_comments):
                        code_lines_count += 1
                        if code_lines_count == 31:
                            first_excess_line = i

            # update brace depth
            brace_depth += braces_delta(code_no_comments)

            # kraj metode kad se vratimo na 0 ili manje
            if brace_depth <= 0:
                if code_lines_count > 30:
                    metode.append((method_start_line, method_sig, code_lines_count, first_excess_line or i))
                in_method = False
                brace_depth = 0
                pending_open_brace = False

    return lines, preduge_linije, viselinijske_instr, metode

def provjeri_direktorij(putanja):
    total_penalty = 0
    total_issues = 0

    for root, _, files in os.walk(putanja):
        for file in files:
            if not file.endswith(".java"):
                continue

            put = os.path.join(root, file)
            lines, preduge, vise_instr, metode = provjeri_datoteku(put)

            if not preduge and not vise_instr and not metode:
                continue

            file_penalty = 0
            print(f"\n📄 {put}")

            if preduge:
                for ln, length in preduge:
                    file_penalty += PENALTY_LONG_LINE
                    total_issues += 1
                    print(f"\n  ⚠️ Linija {ln} ima {length} znakova (>120)  | penalty +{PENALTY_LONG_LINE}")
                    print(snippet(lines, ln))

            if vise_instr:
                for ln, semis in vise_instr:
                    file_penalty += PENALTY_MULTI_INSTR
                    total_issues += 1
                    print(f"\n  ⚠️ Linija {ln} sadrži više od jedne instrukcije (;={semis})  | penalty +{PENALTY_MULTI_INSTR}")
                    print(snippet(lines, ln))

            if metode:
                for start_ln, sig, count, excess_ln in metode:
                    file_penalty += PENALTY_METHOD_TOO_LONG
                    total_issues += 1
                    print(f"\n  ⚠️ Metoda od linije {start_ln} ima {count} linija koda (>30)  | penalty +{PENALTY_METHOD_TOO_LONG}")
                    print(f"     Potpis: {sig}")
                    print(f"     Prvi prelazak limita na liniji: {excess_ln}")
                    print(snippet(lines, excess_ln))

            total_penalty += file_penalty
            print(f"\n  🧾 Ukupno penala za datoteku: {file_penalty}")

    print("\n====================")
    print(f"✅ Ukupno pronađenih problema: {total_issues}")
    print(f"🏁 Ukupno penalizacijskih bodova: {total_penalty}")
    print("====================")

if __name__ == "__main__":
    direktorij = input("Unesi put do direktorija s kodom: ").strip().strip('"')
    if not os.path.isdir(direktorij):
        print("❌ Nije pronađen direktorij.")
    else:
        provjeri_direktorij(direktorij)
