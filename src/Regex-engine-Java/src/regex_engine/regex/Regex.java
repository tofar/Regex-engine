package regex_engine.regex;

import regex_engine.parse.Parse;
import regex_engine.parse.SyntaxError;
import regex_engine.compile.Compile;

import java.util.HashMap;
import java.util.HashSet;

public class Regex {

    private String regex;
    private StateMatch match;

    public Regex(){

    }
    public Regex(String r) throws SyntaxError {
        regex = r;
        match = new StateMatch(Compile.compile(Parse.parse(regex)));
    }

    @Override
    public String toString() {
        return "/" + regex + "/";
    }

    // 正则匹配
    private boolean matches(String input) throws SyntaxError {
        boolean isfinalstate = true;
        for (int i = 0; i < input.length(); i++) {
            int judge = judgeFunction();
            if (judge == 0) {
                if (!match.accept(input.charAt(i))) {
                    isfinalstate = false;
                    break;
                }
            } else if (judge == 1) {
                if (!match.acceptZeroOrMore(input.substring(i)))
                    isfinalstate = false;
                break;
            } else if (judge == 2) {
                if (!match.acceptZeroOrOne(input.substring(i)))
                    isfinalstate = false;
                break;
            } else if (judge == 3) {
                if (!match.acceptOneOrMore(input.substring(i)))
                    isfinalstate = false;
                break;
            } else if (judge == 4) {
                int concat_state = match.acceptConcat(input.substring(i));
                if (concat_state == 0) {
                    isfinalstate = false;
                    break;
                }else{
                    i = i + concat_state - 1;
                }
            }else if (judge == 5) {
                if (!match.acceptOneCharRange(input.substring(i)))
                    isfinalstate = false;
                break;
            } else
                throw new SyntaxError("The engine has some problems.");

        }

        if (isfinalstate && match.isOnFinalState())
            isfinalstate = true;
//        boolean result = match.isOnFinalState();
        match.reset();
        return isfinalstate;
    }

    // 测试匹配
    public void test(String input) throws SyntaxError {
        System.out.println(input + ": " + this.matches(input));
    }

    private int judgeFunction() {

        int min = (Integer)match.getCurrentStates().toArray()[0];

        HashMap<String, HashSet<Integer>> state = match.getStateChart().getConnections().get(min);
        if (state.containsKey("**"))
            return 1;
        else if (state.containsKey("??"))
            return 2;
        else if (state.containsKey("++"))
            return 3;
        else if (state.containsKey("()"))
            return 4;
        else if (state.containsKey("{}"))
            return 5;
        else
            return 0;
    }
}
