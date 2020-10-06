#include <iostream> // 标准输入输出流
#include <fstream> // 文件读取写入流
#include <map>
#include <string>

using namespace std;

//D:\\LAB\\Drh_Compiler\\SimpleLA\\input.cpp

char CHAR; // 当前字符
char TOKEN[100]; // 当前字符串-->单词
int num; // 记录当前TOKEN的下标 字符数 count = num + 1
bool eof = false; // true 表明文章读到末尾，无需 UNGETCH()

bool ISEOF(fstream &inFile){
    return inFile.peek() == EOF;
}

string TOSTRING(){
    string str = TOKEN;
    return str.substr(0,num+1);
}

void GETCHAR(fstream &inFile){ // 指针指向当前读的字符
    inFile.get(CHAR);
}

void UNGETCH(fstream &inFile){ // 指针回退一格
    inFile.seekg(-1, ios::cur);
}

void GETNBC(fstream &inFile){ // 指针指向最后一个空格
    GETCHAR(inFile);
    while (CHAR == ' '){
        GETCHAR(inFile);
    }
    UNGETCH(inFile);
}

void CAT(){
    // 连接单词字符串和当前读入的字符，如：TOKEN='BEGI', CHAR='N'，连接为‘BEGIN’
    TOKEN[++num] = CHAR;
}

bool ISLETTER(){
    return isalpha(CHAR) != 0;
}

bool ISDIGIT(){
    return isdigit(CHAR);
}

bool RESERVE(map<string, string> RESERVE_LIST){
    // TOKEN保存的字符串为保留字 返回true；为标识符，返回false
    map<string, string>::iterator iter;
    iter = RESERVE_LIST.find(TOSTRING());
    if(iter!=RESERVE_LIST.end()){
        return true;
    } else {
        return false;
    }
}

int ATOI(){// 字符串-->数字
    string str = TOKEN;
    return stoi(str.substr(0,num+1));
}

void ERROR(){// 出错处理
    cout << "Unknown" << endl;
}

bool ISPLUSSY(){// +
    return CHAR == '+';
}

bool ISSTARSY(){// *
    return CHAR == '*';
}

bool ISCOMMASY(){// ,
    return CHAR == ',';
}

bool ISLPARSY(){// (
    return CHAR == '(';
}

bool ISRPARSY(){// )
    return CHAR == ')';
}

bool ISASSIGNSY(){// =
    return CHAR == '=';
}

bool ISCOLONSY(){// :
    return CHAR == ':';
}

void LetterCase(fstream &inFile, map<string, string> RESERVE_LIST){
    while(ISLETTER() || ISDIGIT()){
        CAT();
        if (ISEOF(inFile)){
            eof = true;
            break;
        }
        GETCHAR(inFile);
    }
    if (!eof){
        eof = false;
        UNGETCH(inFile); // 多读了一位非字母数字的字符
    }

    if(RESERVE(RESERVE_LIST)){
        map<string, string>::iterator iter;
        iter = RESERVE_LIST.find(TOSTRING());
        cout << iter->second << endl;
    } else {
        cout << "Ident(" + TOSTRING() + ")" << endl;
    }
}

void DigitCase(fstream &inFile){
    while (ISDIGIT()){
        CAT();
        if (ISEOF(inFile)){
            eof = true;
            break;
        }
        GETCHAR(inFile);
    }
    if (!eof){
        eof = false;
        UNGETCH(inFile); // 多读了一位非字母数字的字符
    }
    cout << "Int(" + to_string(ATOI()) + ")" << endl;
}

void ColonCase(fstream &inFile){
    if(!ISEOF(inFile)){
        GETCHAR(inFile);
        if(ISASSIGNSY()){
            cout << "Assign" << endl;
        }else {
            UNGETCH(inFile);
            cout << "Colon" << endl;
        }
    }else{
        cout << "Colon" << endl;
    }
}

int main(int argc, char *argv[]) {
    map<string, string> RESERVE_LIST = {{"BEGIN", "Begin"},
                                           {"END", "End"},
                                           {"FOR", "For"},
                                           {"IF", "If"},
                                           {"THEN", "Then"},
                                           {"ELSE", "Else"}};
    char *filepath = argv[1];
    fstream inFile;
    inFile.open(filepath);
    while (!ISEOF(inFile)){
        GETCHAR(inFile);
        num = -1;
        if (ISLETTER()){
            LetterCase(inFile, RESERVE_LIST);
        }
        else if (ISDIGIT()){
            DigitCase(inFile);
        }
        else if (ISPLUSSY()){
            cout << "Plus" << endl;
        }
        else if (ISSTARSY()){
            cout << "Star" << endl;
        }
        else if (ISCOMMASY()){
            cout << "Comma" << endl;
        }
        else if (ISLPARSY()){
            cout << "LParenthesis" << endl;
        }
        else if (ISRPARSY()){
            cout << "RParenthesis" << endl;
        }
        else if (ISCOLONSY()){
            ColonCase(inFile);
        }
        else if (CHAR != ' ' && CHAR != '\r' && CHAR != '\n'){
            ERROR();
            break;
        }
    }
    inFile.close();
    return 0;
}
