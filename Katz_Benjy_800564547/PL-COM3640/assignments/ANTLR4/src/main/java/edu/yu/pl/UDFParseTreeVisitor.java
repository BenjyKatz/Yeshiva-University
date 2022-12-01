package edu.yu.pl;


import java.util.HashMap;
import java.util.List;
public class UDFParseTreeVisitor extends UDFLanguageBaseVisitor<Object> {

    public static Object VOID = "UNDEFINED";

    private final HashMap<String, Object> referenceFields = new HashMap<>();

    private final HashMap<String, UserDefinedField> userDefinedFields = new HashMap<>();

    public UDFParseTreeVisitor() {
    }

    public void setReferenceField(String fieldName, Object value)
    {
        this.referenceFields.put(fieldName, value);
    }

    public void setReferenceField(String fieldName, UserDefinedField value)
    {
        this.userDefinedFields.put(fieldName, value);
    }

    private boolean isInt(Object o) { return o instanceof Integer; }

    /********** Visitor Overrides ***************/
    @Override
    protected Object defaultResult() {
        return VOID;
    }

    /********************
     numExpr
     ********************/

    @Override
    public Object visitNegNumExpr(UDFLanguageParser.NegNumExprContext ctx) {
        // Negate the value of the child expression
        var value = (Number)visit(ctx.numExpr());
        if (isInt(value))
            return value.intValue() * -1;
        else
            return value.doubleValue() * -1;
     }

    @Override
    public Object visitMulDiv(UDFLanguageParser.MulDivContext ctx) {
        var left = (Number)visit(ctx.left);
        var right = (Number)visit(ctx.right);

        var multiply =  ctx.op.getText().equals("*");

        Number returnValue;
        if (isInt(left) && isInt(right)) {
            if (multiply)
                returnValue = left.intValue() * right.intValue();
            else {
                // Do not perform integer division, convert to Double instead
                double d = left.doubleValue() / right.doubleValue();
                if (d == Math.floor(d))
                    returnValue = ((Double)d).intValue();
                else
                    returnValue = d;
            }
        }
        else {
            returnValue = (multiply)
                    ? left.doubleValue() * right.doubleValue()
                    : left.doubleValue() / right.doubleValue();
        }
        
        return returnValue;
    }

    @Override
    public Object visitAddSub(UDFLanguageParser.AddSubContext ctx) {
        var left = (Number)visit(ctx.left);
        var right = (Number)visit(ctx.right);

        var add =  ctx.op.getText().equals("+");

        Number returnValue;
        if (isInt(left) && isInt(right)) {
            // Do not perform integer division, convert to Double instead.
            returnValue = (add)
                    ? left.intValue() + right.intValue()
                    : left.intValue() - right.intValue();
        }
        else {
            returnValue = (add)
                    ? left.doubleValue() + right.doubleValue()
                    : left.doubleValue() - right.doubleValue();
        }

        return returnValue;
    }

    @Override
    public Object visitNumConstant(UDFLanguageParser.NumConstantContext ctx) {
        // Convert the token text to type Integer or Double
        double result = Double.parseDouble(ctx.getText());

        if (!Double.isInfinite(result) && !ctx.getText().contains(".") && result == Math.floor(result))
            return ((Double)result).intValue();

        return result;
    }

    @Override
    public Object visitParensNumExpr(UDFLanguageParser.ParensNumExprContext ctx) {
        return visit(ctx.numExpr());
    }

    @Override
    public Object visitAbsValue(UDFLanguageParser.AbsValueContext ctx) {
        var value = (Number)visit(ctx.numExpr());
        return isInt(value) ? Math.abs(value.intValue()) : Math.abs(value.doubleValue());
    }

    /********************
    strExpr
    ********************/

    @Override
    public Object visitStrConstant(UDFLanguageParser.StrConstantContext ctx) {
        // Remove quotes from around the string constant
        String text = ctx.getText().substring(1);
        return text.substring(0, text.length()-1);
    }

    @Override
    public Object visitConcat(UDFLanguageParser.ConcatContext ctx) {
        return ((String)visit(ctx.left)).concat((String)visit(ctx.right));
    }

    @Override
    public Object visitParensStrExpr(UDFLanguageParser.ParensStrExprContext ctx) {
        return visit(ctx.strExpr());
    }

    @Override
    public Object visitStr(UDFLanguageParser.StrContext ctx){
        var value = (Number)visit(ctx.numExpr());
        if (isInt(value))
            return  ""+value.intValue();
        else
            return ""+value.doubleValue();
    }

    @Override
    public Object visitJoin(UDFLanguageParser.JoinContext ctx){
        String del = (ctx.del).getText();
        List<UDFLanguageParser.StrExprContext> words = ctx.strExpr();
        String stringToReturn = "";
        stringToReturn = stringToReturn + visit(words.remove(0));
        for (UDFLanguageParser.StrExprContext word:words) {
            stringToReturn = stringToReturn+ del + visit(word);
        }
        return stringToReturn;
    }
    @Override
    public Object visitIf(UDFLanguageParser.IfContext ctx){
       // System.out.println("Here!");
        //System.out.println("ifff"+ctx.getText());
        String stringToReturn = "";
        var val = visit(ctx.boolExpr());
        boolean test = (boolean)val;
       // System.out.println(test);
        if(test){
           // System.out.println(visit(ctx.strExpr(0)));
            return visit(ctx.strExpr(0));
        }
       // System.out.println(visit(ctx.strExpr(1)));
        return visit(ctx.strExpr(1));
    }


    /********************
     boolExpr
     *******************/
    @Override
    public Object visitBoolConstant(UDFLanguageParser.BoolConstantContext ctx) {
        // Remove quotes from around the string constant
        //System.out.println("in bool const");
        boolean bool = false;
        if(ctx.getText().equals("True")||ctx.getText().equals("true")||ctx.getText().equals("TRUE")){

            bool = true;
        }
        else{
            bool = false;
        }
        return bool;
    }
    @Override
    public Object visitStrCompare(UDFLanguageParser.StrCompareContext ctx) {
        //System.out.println("Comparing string");
        String comaprator = (ctx.compare).getText();
        String left = ""+visit(ctx.left);
        String right = ""+visit(ctx.right);
        //System.out.println(left+" "+right);
        int comparison = left.compareTo(right);
        if(comparison>0){
            if(comaprator.equals(">")) return true;
            if(comaprator.equals(">=")) return true;
            if(comaprator.equals("<>")) return true;
            return false;
        }
        else if(comparison == 0){
            if(comaprator.equals("=")) return true;
            if(comaprator.equals(">=")) return true;
            if(comaprator.equals("<=")) return true;
            return false;
        }
        else if(comparison< 0){
            if(comaprator.equals("<")) return true;
            if(comaprator.equals("<=")) return true;
            if(comaprator.equals("<>")) return true;
            return false;
        }

        return false;
    }
    @Override
    public Object visitNumCompare(UDFLanguageParser.NumCompareContext ctx) {
        //System.out.println("in Num compare");
        String comaprator = (ctx.compare).getText();
        var left = (Number)visit(ctx.left);
        var right = (Number)visit(ctx.right);

        if(left.doubleValue()>right.doubleValue()){
            if(comaprator.equals(">"))return true;
            if(comaprator.equals(">="))return true;
            if(comaprator.equals("<>"))return true;
            return false;
        }
        if(left.doubleValue()<right.doubleValue()){
            if(comaprator.equals("<"))return true;
            if(comaprator.equals("<="))return true;
            if(comaprator.equals("<>"))return true;
            return false;
        }
        if(left.doubleValue() == right.doubleValue()){
            if(comaprator.equals("="))return true;
            if(comaprator.equals(">="))return true;
            if(comaprator.equals("<="))return true;
            return false;
        }
        return false;
    }
    @Override
    public Object visitNot(UDFLanguageParser.NotContext ctx) {
        Boolean bool = (Boolean)visit(ctx.boolExpr());
        return !bool;
    }
    @Override
    public Object visitAnd(UDFLanguageParser.AndContext ctx) {
        Boolean firstArg = (Boolean)visit(ctx.boolExpr(0));
        Boolean secondArg = (Boolean)visit(ctx.boolExpr(1));
        return (firstArg & secondArg);
    }
    @Override
    public Object visitOr(UDFLanguageParser.OrContext ctx) {
        Boolean firstArg = (Boolean)visit(ctx.boolExpr(0));
        Boolean secondArg = (Boolean)visit(ctx.boolExpr(1));
        return (firstArg || secondArg);
    }
    @Override
    public Object visitIn(UDFLanguageParser.InContext ctx) {
        List<UDFLanguageParser.StrExprContext> words = ctx.strExpr();
        String wordToCheck = "";
        wordToCheck = "" + visit(words.remove(0));
        for (UDFLanguageParser.StrExprContext word:words) {
            if(wordToCheck.equals("" + visit(word))) return true;
        }
        return false;
    }
    @Override
    public Object visitParensBool(UDFLanguageParser.ParensBoolContext ctx) {
        return visit(ctx.boolExpr());
    }
        /********************
         Field name resolutions
         ********************/

    @Override
    public Object visitNumField(UDFLanguageParser.NumFieldContext ctx) {
        Object entry = resolveFieldName(ctx.getText());

        // Check that the value is indeed numeric
        return entry instanceof Number ? entry : VOID;
    }

    private Object resolveFieldName(String field) {
        Object entry = VOID;

        // Check the user fields first, so that we have the ability
        // to override the reference data
        if (userDefinedFields.containsKey(field)) {
            UserDefinedField userDefinedField = userDefinedFields.get(field);
            entry = userDefinedField.evaluate(this);

            // Remove the entry from the UDF and place it in the reference data
            // So that next time there will be no need to resolve it again
            this.referenceFields.put(field, entry);
            this.userDefinedFields.remove(field);

        } else if (this.referenceFields.containsKey(field)) {
            entry = this.referenceFields.get(field);
        }

        return entry;
    }

    @Override
    public Object visitStrField(UDFLanguageParser.StrFieldContext ctx) {
        Object entry = resolveFieldName(ctx.getText());

        // Automatically convert Numbers to strings
        return entry instanceof Number ? entry.toString() : entry;
    }
}
