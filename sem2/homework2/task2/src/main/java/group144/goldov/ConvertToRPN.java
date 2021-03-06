package group144.goldov;

/** The class that implements convert to a RPN with Dijkstra's sorter machine **/
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
                    while (!stack.isEmpty() && (stack.peek() == '*' || stack.peek() == '/')) {
                        out.append(' ');
                        out.append(stack.pop());
                    }
                    out.append(' ');
                    stack.push(c);
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
                    while (stack.peek() != '(') {
                        out.append(' ');
                        out.append(stack.pop());
                    }
                    stack.pop();
                    break;
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
