BEGIN X:NUMBER y:NUMBER THEN x:=1234 y :=x+2*5 IF x （

Begin\nIdent(x)\nColon\nIdent(NUMBER)\nIdent(y)\nColon\nIdent(NUMBER)\nIdent(THEN)\nIdent(x)\n
        Assign\nInt(1234)\nIdent(y)\nAssign\nIdent(x)\nPlus\nInt(2)\nStar\nInt(5)\nIdent(IF)\nIdent(x)\nUnknown\n"

"stdoutDiff":"  Begin\n  Ident(x)\n  Colon\n  Ident(NUMBER)\n  Ident(y)\n  Colon\n  Ident(NUMBER)\n"
"- Ident(THEN)\n+ Then\n  "
"Ident(x)\n  Assign\n  Int(1234)\n  Ident(y)\n  Assign\n  Ident(x)\n  Plus\n  Int(2)\n  Star\n  Int(5)\n"
"- Ident(IF)\n+ If\n  Ident(x)\n  Unknown\n"