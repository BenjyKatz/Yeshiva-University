grammar UDFLanguage;

/* Start Variable */
userField : numExpr  # NumExpression
          | strExpr  # StrExpression
          | boolExpr #BoolExpression
          | EOF      # EndOfInput
          ;

// Possible expressions in order of precedence
numExpr : '-' numExpr                              # NegNumExpr
        | left=numExpr op=('*'|'/') right=numExpr  # MulDiv
        | left=numExpr op=('+'|'-') right=numExpr  # AddSub
        | '(' numExpr ')'                          # ParensNumExpr
        | 'ABS' '(' numExpr ')'                    # AbsValue
        | NUMBER_CONSTANT                          # NumConstant
        | FIELD_NAME                               # NumField
        ;

strExpr : left=strExpr '+' right=strExpr           # Concat
        | '(' strExpr ')'                          # ParensStrExpr
        | 'STR' '(' numExpr ')'                    # Str
        | 'JOIN' '(' del=('-'|'/') ',' strExpr ',' strExpr (',' strExpr)* ')' #Join
        | 'IF''('boolExpr','strExpr','strExpr')'   #If
        | STRING_CONSTANT                          # StrConstant
        | FIELD_NAME                               # StrField

        ;
boolExpr : left=strExpr compare=('='|'>'|'>='|'<'|'<='|'<>') right=strExpr # StrCompare
         | left=numExpr compare=('='|'>'|'>='|'<'|'<='|'<>') right=numExpr # NumCompare
         | '(' boolExpr ')'                                                #ParensBool
         | left=boolExpr 'AND' right=boolExpr                              #And
         | left=boolExpr 'OR' right=boolExpr                               #Or
         | strExpr 'IN' '('strExpr (','strExpr)*')'                        #In
         | 'NOT''('boolExpr ')'                                            #Not
         | ('TRUE'|'True'|'true'|'FALSE'|'False'|'false')                  #BoolConstant
         ;

/* Tokens */
NUMBER_CONSTANT: [0-9]+ ('.' [0-9]+)?;
STRING_CONSTANT: '\'' [a-zA-Z0-9_][a-zA-Z0-9_]* '\'';
FIELD_NAME     : [0-9a-zA-Z_][a-zA-Z0-9_]*;                // variable field name
BOOLEAN_CONSTANT:('TRUE'|'True'|'true'|'FALSE'|'False'|'false');

COMMENT : '//' ~[\r\n]* -> skip;    // skip the rest of the line after comment (//)
WHITE_SPACE: [ \t\n]+ -> skip;      // skip spaces, tabs and newline characters