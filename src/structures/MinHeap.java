package structures;

public class MinHeap {
        private Event[] heap;
        private int size;
        private int capacity;
       public MinHeap(int capacity){
           this.capacity=capacity;
           this.size=0;
           this.heap=new Event[capacity+1];
       }
       public void insert(Event event){
           if(size>=capacity){
               capacity*=2;
               Event []newheap=new Event[capacity+1];
               System.arraycopy(heap,0,newheap,0,heap.length);
               heap=newheap;
           }
           size++;
           heap[size]=event;
            bubbleUp(size);
       }
       public Event deleteMin(){
           if(isEmpty()){
               return null;
           }
           Event min=heap[1];
           heap[1]=heap[size];
           heap[size]=null;
           size--;
           if(size>0){
               bubbleDown(1);
           }
           return min;
       }
       public Event peekMin(){
           if(isEmpty()){
               return null;
           }
           return heap[1];
       }
       public void bubbleDown(int i){
           while(i*2<=size){
               int left=i*2;
               int right=left+1;
               int smallest=i;
               if(left<=size && heap[left].compareTo(heap[smallest])<0){
                   smallest=left;
               }
               if(right<=size && heap[right].compareTo(heap[smallest])<0){
                   smallest=right;
               }
               if(smallest!=i){
                   swap(i,smallest);
                   i=smallest;
               }else{
                   break;
               }
           }
       }
       public void bubbleUp(int i){
           while(i>1){
               int parent=i/2;
               if(heap[i].compareTo(heap[parent])<0){
                   swap(i,parent);
                   i=parent;
               }else{
                   break;
               }
           }
       }
       public void swap(int i,int j){
           Event t=heap[i];
           heap[i]=heap[j];
           heap[j]=t;
       }
       public boolean isEmpty(){
           return size==0;
       }
       public int size(){
           return size;
       }
}
