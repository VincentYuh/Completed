#include <iostream>
#include <queue>
#include <vector>
using namespace std;

class Node {
private:
    int key;

public:
    void Set_key(int x);
    int Get_key();
    Node();
};

void Node::Set_key(int x) {
    key = x;
}

int Node::Get_key() {
    return key;
}

Node::Node() {
    key = -1;
}

class MinHeap {
private:
    vector<Node *> heap;

public:
    void BSTraverse();
    void Add(Node *p);
    void Remove();
    Node *Root();
    void Heapify(int i);
    MinHeap();
};

void MinHeap::BSTraverse() {
    for (int i = 0; i < heap.size(); i++) {
        cout << heap[i]->Get_key()<<" ";
    }
}

void MinHeap::Add(Node *p) {
    heap.push_back(p);
    int i = heap.size() - 1;
    while (i != 0 && heap[(i - 1) / 2]->Get_key() > heap[i]->Get_key()) {
        swap(heap[i], heap[(i - 1) / 2]);
        i = (i - 1) / 2;
    }
}

void MinHeap::Remove() {
    if (heap.empty()) {
        return;
    }

    heap[0] = heap.back();
    heap.pop_back();
    if (!heap.empty()) {
        Heapify(0);
    }
}

void MinHeap::Heapify(int i) {
    int smallest = i;
    int left = 2 * i + 1;
    int right = 2 * i + 2;

    if (left < heap.size() && heap[left]->Get_key() < heap[smallest]->Get_key()) {
        smallest = left;
    }

    if (right < heap.size() && heap[right]->Get_key() < heap[smallest]->Get_key()) {
        smallest = right;
    }

    if (smallest != i) {
        swap(heap[i], heap[smallest]);
        Heapify(smallest);
    }
}

Node *MinHeap::Root() {
    if (heap.empty()) {
        return nullptr;
    }

    return heap[0];
}

MinHeap::MinHeap() {}

int main() {
    int mode, key;
    cin >> mode;

    MinHeap heap;

    if (mode == 3) {
        Node arr[5];
        for (int i = 0; i < 5; i++) {
            arr[i].Set_key(5 - i);
            heap.Add(&arr[i]);
        }
    } else {
        while (cin >> key) {
            Node *temp = new Node;
            temp->Set_key(key);
            heap.Add(temp);
        }
    }

    if (mode == 0) {
        heap.BSTraverse();
    } else if (mode == 1) {
        Node *temp = heap.Root();
        cout << temp->Get_key();
    } else if (mode == 2) {
        heap.Remove();
        heap.BSTraverse();
    } else if (mode == 3) {
        // Heap sort implementation
        for (int i = 0; i < 5; i++) {
            Node *temp = heap.Root();
            heap.Remove();
            cout << temp->Get_key();
        }
    }
    return 0;
}

