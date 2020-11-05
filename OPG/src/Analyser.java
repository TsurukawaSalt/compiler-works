import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Analyser {
    public List<Character> input = new ArrayList<>(); // 输入字符串
    public List<String> Gram = new ArrayList<>(); // 文法集合
    public Map<Character, List<String>> production = new HashMap<>(); // 文法切割
    public Set<Character> VT = new LinkedHashSet<>(); // 终结符集合
    public Set<Character> VN = new LinkedHashSet<>(); // 非终结符集合
    public Map<Character, Set<Character>> FirstVT = new HashMap<>(); // Character 为非终结符 set为终结符集合
    public Map<Character, Set<Character>> LastVT = new HashMap<>();
    public Map<String, Character> table = new HashMap<>(); // 符号优先级表
    public static void main(String[] args) {
        Analyser analyser = new Analyser();
        // 加载文法
        analyser.loadGram();
        // 读文件（仅一行）到字符串 input
        analyser.scanIn(args[0]);
        // 分割文法
        analyser.getProduction();
        // 获得VT VN
        analyser.getVT();
        analyser.getVN();
        // 获得FirstVT LastVT
        analyser.getFirstVTAndLastVT();
        // 获得算符优先矩阵
        analyser.getTable();
        analyser.printTable();
        // 分析
        analyser.analyse();
    }
    public void loadGram(){
        Gram.add("E->E+T|T");
        Gram.add("T->T*F|F");
        Gram.add("F->(E)|i");
    }
    public void getProduction(){
        for (String s : Gram) {
            List<String> list = new ArrayList<>();
            String[] part = s.split("->");
            char leftPart = part[0].toCharArray()[0];
            String[] rightPart = part[1].split("\\|");
            Collections.addAll(list, rightPart); // 右部存到 list 中
            production.put(leftPart, list); // 左部和右部存到 map 中
        }

    }
    public void scanIn(String filePath){
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String str = reader.readLine();
            for(char c: str.toCharArray()){
                input.add(c);
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void getVT(){
        for(String s: Gram){
            for(char c: s.toCharArray()){
                if(c!='-' && c!='>' && c!='|' && !Character.isUpperCase(c)){
                    VT.add(c);
                }
            }
        }
        VT.add('#');
    }
    public void getVN(){
        for(String s: Gram){
            for(char c: s.toCharArray()){
                if(Character.isLetter(c) && Character.isUpperCase(c)){
                    VN.add(c);
                }
            }
        }
    }
    public boolean isVT(Character c){
        return VT.contains(c);
    }
    public boolean isVN(Character c){
        return VN.contains(c);
    }
    public void getFirstVT(Character vn, Set<Character> fvt){
        // A -> a....
        // A -> Ba...
        // A -> B....
        List<String> proList = production.get(vn);
        // proList(E):
        // E+T
        // T
        for(String s: proList){
            char c = s.charAt(0);
            if(isVT(c)){
                // A -> a....
                fvt.add(c);
            }else if(isVN(c)){
                // A -> Ba...
                if(s.length()>1 && isVT(s.charAt(1))){
                    fvt.add(s.charAt(1));
                }
                if(c != vn){
                    getFirstVT(c, fvt);
                }
            }
        }

    }
    public void getLastVT(Character vn, Set<Character> lvt){
        // A -> ....a
        // A -> ...aB
        // A -> ....B
        List<String> proList = production.get(vn);
        for(String s: proList){
            char c = s.charAt(s.length()-1);
            if(isVT(c)){
                lvt.add(c);
            }else if(isVN(c)){ // c 为非终结符
                if(s.length()>1 && isVT(s.charAt(s.length()-2))){
                    lvt.add(s.charAt(s.length()-2));
                }
                if(c!=vn){
                    getLastVT(c, lvt);
                }

            }
        }
    }
    public void getFirstVTAndLastVT(){
        // 遍历每个非终结符
        for(char c: VN){
            Set<Character> fvt = new HashSet<>();
            getFirstVT(c, fvt);
            FirstVT.put(c, fvt);
        }
        for(char c: VN){
            Set<Character> lvt = new HashSet<>();
            getLastVT(c, lvt);
            LastVT.put(c, lvt);
        }
    }
    public void getTable(){
        // 遍历每条规则
        for(char vn: VN){
            for(String s: production.get(vn)){
                // s 产生式右部 例: E+T
                if(s.length()<2){
                    continue;
                }
                char[] list = s.toCharArray();
                for(int i=0; i< list.length-1; i++){
                    if(isVT(list[i]) && isVT(list[i+1])){
                        table.put(list[i]+""+list[i+1], '=');
                    }
                    if(i<= list.length-3 && isVT(list[i]) && isVN(list[i+1]) && isVT(list[i+2])){
                        table.put(list[i]+""+list[i+2], '=');
                    }
                    if(isVT(list[i]) && isVN(list[i+1])){
                        // aB
                        // xi < flv(B)
                        for(Character c: FirstVT.get(list[i+1])){
                            table.put(list[i]+""+c, '<');
                        }
                    }
                    if(isVN(list[i]) && isVT(list[i+1])){
                        // Ba
                        // lvt(B) > a
                        for(Character c: LastVT.get(list[i])){
                            table.put(c+""+list[i+1], '>');
                        }
                    }
                }
            }
        }
        // 特别
        // E' -> #E#
        // # < Fvt(E)
        // Lvt(E) > #
        // # = #
        for(Character c: FirstVT.get('E')){
            table.put("#"+c, '<');
        }
        for(Character c: LastVT.get('E')){
            table.put(c+"#", '>');
        }
        table.put("##", '=');
    }
    public void printTable(){
        System.out.print("  ");
        for(Character c: VT){
            System.out.print(c + " ");
        }
        System.out.println("");
        for(Character c: VT){
            System.out.print(c+" ");
            for(Character d: VT){
                if(table.containsKey(c+""+d)){
                    char rtn = table.get(c+""+d);
                    System.out.print(rtn+" ");
                }else {
                    System.out.print("  ");
                }
            }
            System.out.println("");
        }
    }
    public char getLeftPart(String right){
        for(Map.Entry<Character, List<String>> map: production.entrySet()){
            char left = map.getKey(); // 非终结符
            for(String str: map.getValue()){
                // right = F+F
                // str   = T+F
                // 算符优先算法跳过了单非终结符的规约，故可将非终结符看作一样的
                if(str.length() == right.length()){
                    int i=0;
                    for(i=0; i<right.length(); i++){
                        if(isVN(right.charAt(i)) && isVN(str.charAt(i))){

                        }else if (isVT(right.charAt(i)) && isVT(str.charAt(i))){
                            if (right.charAt(i) == str.charAt(i)){

                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                    if (i == right.length()){
                        return left;
                    }
                }

            }
        }
        return 0;
    }
    public boolean isEnd(List<Character> opStack, int top, int pos){
        // opStack长度=2 topVT为# input.pos为#
        int topVT;
        if(isVN(opStack.get(top))){
            topVT = top - 1;
        }else {
            topVT = top;
        }
        return opStack.get(topVT) == '#' && input.get(pos) == '#';
    }
    public void analyse(){
        // 符号栈
        List<Character> opStack = new ArrayList<>();
        opStack.add('#');
        int top = 0; // 栈顶 top
        int topVT = 0; // 终结符栈顶
        // 输入串
        int pos=0;
        input.add('#');

        char in = input.get(pos);
        char c = opStack.get(top);
        boolean err = false;
        while(!isEnd(opStack, top, pos)){
//            System.out.println("----new round----");
//            System.out.println("op栈"+opStack);
//            System.out.println("op栈顶->"+c);
            // 获取终结符栈顶
            if(isVN(c)){
                topVT = top - 1;
            }else{
                topVT = top;
            }

//            System.out.println("终结符栈顶->"+opStack.get(topVT));
//            System.out.println("输入串->"+in);
            // 判断是否存在优先级
            if (!table.containsKey(opStack.get(topVT)+""+in)){
                // 无法比较，输出 E，结束分析程序
                System.out.println("E");
                break;
            }

            // 归约
            while (table.get(opStack.get(topVT)+""+in).equals('>')){
//                 System.out.print("归约->");
                if (isEnd(opStack, top, pos)){
                    break;
                }
                char tmp = 0;

                // 找到 ‘<'
                do {
                    tmp = opStack.get(topVT);
                    if(isVT(opStack.get(topVT-1)) || opStack.get(topVT-1) == '#'){
                        topVT = topVT - 1;
                    }else{
                        topVT = topVT - 2;
                    }
                }while (!table.get(opStack.get(topVT)+""+tmp).equals('<'));

                // 得到素短语
                StringBuilder str = new StringBuilder();
                for(int i=topVT+1; i<opStack.size(); i++){
                    str.append(opStack.get(i));
                }
                String rightPart = str.toString();

                for (int i=top; i>topVT; i--){
                    opStack.remove(i);
                }
//                System.out.println("");
//                System.out.println("修改后的op栈"+opStack);
//                System.out.print("试图归约->"+rightPart);
                char left = getLeftPart(rightPart);
                if(left!=0){
                    opStack.add(left);
//                    System.out.println(" 规约成功-->"+left);
//                    System.out.println("op栈"+opStack);
                    top = topVT + 1;
                }else{
                    // 归约失败，跳出归约循环
                    System.out.println("RE");
                    err = true;
                    break;
                }
                System.out.println("R");
                if (!table.containsKey(opStack.get(topVT)+""+in)){
                    // 无法比较
                    System.out.println("E");
                    err = true;
                    break;
                }
            }
            if (err){
                break;
            }
            // 判断是否结束
            if(isEnd(opStack, top, pos)){
                break;
            }
            // 移进
            if(!table.containsKey(opStack.get(topVT)+""+in)){
                // 无法比较优先级,跳出循环，结束分析程序
                System.out.println("E");
                break;
            }else if (table.get(opStack.get(topVT)+""+in).equals('<') || table.get(opStack.get(topVT)+""+in).equals('=')){
                // System.out.println("移进：");
                System.out.println("I"+in);
                opStack.add(in);
                top++;
                pos++;
                c = opStack.get(top);
                in = input.get(pos);
//                System.out.println("op栈"+opStack);
            }
        }

    }
}
