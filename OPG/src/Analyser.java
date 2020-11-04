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
        // # < vt
        // vt > #
        for(char c: VT){
            table.put("#"+c, '<');
            table.put(c+"#", '>');
        }
        table.put("##", '=');
    }
//    public void printTable(){
//        System.out.print("  ");
//        for(Character c: VT){
//            System.out.print(c + " ");
//        }
//        System.out.println("");
//        for(Character c: VT){
//            System.out.print(c+" ");
//            for(Character d: VT){
//                if(table.containsKey(c+""+d)){
//                    char rtn = table.get(c+""+d);
//                    System.out.print(rtn+" ");
//                }else {
//                    System.out.print("  ");
//                }
//            }
//            System.out.println("");
//        }
//    }
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
//        int count = 0;
        boolean err = false;
        while(!(opStack.size()==2 && isVN(opStack.get(top)) && in=='#')){
//            count ++;
//            if(count == 10){
//                break;
//            }
            // print
//            System.out.println("new round");
//            System.out.println("op栈顶字符："+c);
//            System.out.println("input栈顶：" + in);
            if(isVT(c) || c == '#'){
                topVT = top;
            }else{
                topVT = top-1;
            }

            // 判断是否存在优先级
            if (!table.containsKey(opStack.get(topVT)+""+in)){
                System.out.println("E");
                break;
            }
            // 归约
            while (table.get(opStack.get(topVT)+""+in).equals('>')){
                // print
//                System.out.println("归约：");
                // 找到 ‘<'
                if (opStack.size()==2  && isVN(opStack.get(top))&& in == '#'){
                    break;
                }
                char tmp = 0;

                do {
                    tmp = opStack.get(topVT);
                    if(isVT(opStack.get(topVT-1)) || opStack.get(topVT-1) == '#'){
                        topVT = topVT - 1;
                    }else{
                        topVT = topVT - 2;
                    }
                }while (!table.get(opStack.get(topVT)+""+tmp).equals('<'));

                StringBuilder str = new StringBuilder();
                // 得到素短语
                for(int i=topVT+1; i<opStack.size(); i++){
                    str.append(opStack.get(i));
                }
                // 修改opStack
                for(int i=topVT+1; i<opStack.size(); i++){
                    opStack.remove(topVT+1);
                }

                String rightPart = str.toString();
                char left = getLeftPart(rightPart);
                if(left!=0){
                    opStack.add(left);
                    top = topVT + 1;
                }else{
                    System.out.println("RE");
                    err = true;
                    break;
                }
                System.out.println("R");
            }
            if (err){
                break;
            }
            // 判断是否结束
            if(opStack.size() == 2 && isVN(opStack.get(top)) && in=='#'){
                break;
            }
            // 移进
            if(table.get(opStack.get(topVT)+""+in).equals('<') || table.get(opStack.get(topVT)+""+in).equals('=')){
                //print
//                System.out.println("移进：");
                System.out.println("I"+in);
                opStack.add(in);
                pos++;
                top++;
                c = opStack.get(top);
                in = input.get(pos);
            }else if(!table.get(opStack.get(topVT)+""+in).equals('>')){
                System.out.println("RE");
            }

            // 判断是否结束
            if(opStack.size() == 2 && isVN(opStack.get(top)) && in=='#'){
                break;
            }
//            System.out.println("op栈顶字符："+c);
//            System.out.println("input栈顶：" + in);
        }

    }
    public char getLeftPart(String right){
        for(Map.Entry<Character, List<String>> map: production.entrySet()){
            char left = map.getKey(); // 非终结符
            for(String str: map.getValue()){
                if(right.equals(str)){
                    return left;
                }
            }
        }
        return 0;
    }
}
