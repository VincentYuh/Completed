#include <iostream>
using namespace std;

class Node {
private:
    int key;
    Node* p_next;

public:
    void Set_key(int x);
    int Get_key();
    void Set_p(Node* p);
    Node* Get_p();
    Node();
};

Node::Node() {
    key = -1;
    p_next = NULL;
}

void Node::Set_key(int x) {
    key = x;
}

int Node::Get_key() {
    return key;
}

void Node::Set_p(Node* p) {
    p_next = p;
}

Node* Node::Get_p() {
    return p_next;
}

class HashTable {
private:
    int size;
    Node** table;

    // Declaration of the hash function
    int hash(int key);

public:
    void CreateTable(int divisor);
    Node* Search(int key);
    void Add(Node* temp);
    void Remove(int key);
    int Get_Size();
    HashTable();
    void PrintTable();
};

HashTable::HashTable() {
    table = NULL;
    size = 0;
}

void HashTable::CreateTable(int divisor) {
    size = divisor;
    table = new Node*[size];
    for (int i = 0; i < size; i++) {
        table[i] = NULL;
    }
}

int HashTable::hash(int key) {
    return key % size; // Simple modulo-based hash function
}

Node* HashTable::Search(int key) {
    int index = hash(key);
    Node* current = table[index];
    while (current != NULL) {
        if (current->Get_key() == key) {
            return current;
        }
        current = current->Get_p();
    }
    return NULL;
}

void HashTable::Add(Node* temp) {
    int index = hash(temp->Get_key());
    temp->Set_p(table[index]);
    table[index] = temp;
}

void HashTable::Remove(int key) {
    int index = hash(key);
    if (table[index] == NULL) {
        return;
    }
    if (table[index]->Get_key() == key) {
        Node* temp = table[index];
        table[index] = table[index]->Get_p();
        delete temp;
        return;
    }
    Node* current = table[index];
    while (current->Get_p() != NULL) {
        if (current->Get_p()->Get_key() == key) {
            Node* temp = current->Get_p();
            current->Set_p(temp->Get_p());
            delete temp;
            return;
        }
        current = current->Get_p();
    }
}

int HashTable::Get_Size() {
    return size;
}

void HashTable::PrintTable() {
    Node* temp = NULL;
    for (int i = 0; i < size; i++) {
        temp = table[i];
        while (temp != NULL) {
            cout << temp->Get_key() << '\n';
            temp = temp->Get_p();
        }
    }
}

int main() {
    int mode, temp;
    int key;
    int divisor;
    Node* Student;
    HashTable x;
    cin >> mode >> key >> divisor;
    x.CreateTable(divisor);
    while (cin >> temp) {
        Student = new Node;
        Student->Set_key(temp);
        x.Add(Student);
    }
    if (mode == 0) {
        cout << x.Get_Size();
    }
    else if (mode == 1) {
        x.PrintTable();
    }
    else if (mode == 2) {
        Student = x.Search(key);
        if (Student == NULL) {
            cout << -1;
        }
        else {
            cout << Student->Get_key();
        }
    }
    else if (mode == 3) {
        x.Remove(key);
        x.PrintTable();
    }
    return 0;
}
