#include <iostream> // 标准输入输出流
#include <fstream> // 文件读取写入流
#include <map>

using namespace std;

fstream inFile;
char CHAR; // 当前字符
char TOKEN[100]; // 当前字符串-->单词
int num; // 记录当前单词TOKEN中字符max字符下标
bool eof = false;

bool ISEOF(){
    return inFile.peek() == EOF;
}

void GETCHAR(){ // 指针指向当前读的字符
    inFile.get(CHAR);
}

void UNGETCH(){ // 指针回退一格
    inFile.seekg(-1, ios::cur);
}

void GETNBC(){ // 指针指向最后一个空格
    GETCHAR();
    while (CHAR == ' '){
        GETCHAR();
    }
    UNGETCH();
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
    string str = TOKEN;
    map<string, string>::iterator iter;
    iter = RESERVE_LIST.find(str);
    if(iter!=RESERVE_LIST.end()){
        return true;
    } else {
        return false;
    }
}

int ATOI(){
    // 字符串-->数字
    string str = TOKEN;

    return stoi(str.substr(0,num+1));
}

void ERROR(){
    // 出错处理
}

bool ISPLUSSY(){
    return CHAR == '+';
}// +

bool ISSTARSY(){
    return CHAR == '*';
}// *

bool ISCOMMASY(){
    return CHAR == ',';
}// ,

bool ISLPARSY(){
    return CHAR == '(';
}// (

bool ISRPARSY(){
    return CHAR == ')';
}// )

bool ISASSIGNSY(){
    return CHAR == '=';
}// =

bool ISCOLONSY(){
    return CHAR == ':';
}// :


int main(int argc, char *argv[]) {
    map<string, string> RESERVE_LIST = {{"BEGIN", "Begin"},
                                           {"END", "End"},
                                           {"FOR", "For"},
                                           {"IF", "If"},
                                           {"THEN", "Then"},
                                           {"ELSE", "Else"}};
    char *filepath = argv[1];
    inFile.open(filepath);
    while (!ISEOF()){
        GETCHAR();
        num = -1;
        if (ISLETTER()){
            while(ISLETTER() || ISDIGIT()){
                CAT();
                if (ISEOF()){
                    eof = true;
                    break;
                }
                GETCHAR();
            }
            if (!eof){
                UNGETCH(); // 多读了一位非字母数字的字符
            }

            string str = TOKEN;
            if(RESERVE(RESERVE_LIST)){
                map<string, string>::iterator iter;
                iter = RESERVE_LIST.find(str);
                cout << iter->second << endl;
            } else {
                cout << "Ident(" + str.substr(0,num+1) + ")" << endl;
            }
        }
        else if (ISDIGIT()){
            while (ISDIGIT()){
                CAT();
                if (ISEOF()){
                    eof = true;
                    break;
                }
                GETCHAR();
            }
            if (!eof){
                UNGETCH(); // 多读了一位非字母数字的字符
            }
            cout << "Int(" + to_string(ATOI()) + ")" << endl;
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
            if(!ISEOF()){
                GETCHAR();
                if(ISASSIGNSY()){
                    cout << "Assign" << endl;
                }else {
                    UNGETCH();
                    cout << "Colon" << endl;
                }
            }else{
                cout << "Colon" << endl;
            }
        }
        else if (CHAR != ' ' && CHAR != '\r' && CHAR != '\n'){
            cout << "Unknown" << endl;
            break;
        }
    }
    return 0;
}
