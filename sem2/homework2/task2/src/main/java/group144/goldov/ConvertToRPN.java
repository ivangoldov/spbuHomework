package group144.goldov;

/** The class that implements convertion to a RPN with Dijkstra's sorter machine **/
public class ConvertToRPN {
    /** A method that converts an expression to reversed polish notation **/
    public String convert(String inputString) throws NullPointerException {
        char[] string = inputString.toCharArray();
        StackList<Character> stack = new StackList<>();
        StringBuilder out = new StringBuilder();
        for (char c : string) {
            switch (c) {
                case '+':
                case '-':
                    try {
                        while (!stack.isEmpty() && (stack.peek() == '*' || stack.peek() == '/')) {
                            out.append(' ');
                            out.append(stack.pop());
                        }
                        out.append(' ');
                        stack.push(c);
                    }
                    catch (NullPointerException e) {
                        System.out.println("You entered wrong expression");
                    }
                    break;
                case '*':
                case '/':
                    out.append(' ');
                    stack.push(c);
                    break;
                case '(':
                    stack.push(c);
                    break;
                case ')':
                    try {
                        while (stack.peek() != '(') {
                            out.append(' ');
                            out.append(stack.pop());
                        }
                        stack.pop();
                        break;
                    }
                    catch (NullPointerException e) {
                        System.out.println("You entered wrong expression, check parenthesis");
                    }
                default:
                    out.append(c);
                    break;
            }
        }
        while (!stack.isEmpty()) {
            out.append(' ');
            out.append(stack.pop());
        }
        return out.toString();
    }
}
