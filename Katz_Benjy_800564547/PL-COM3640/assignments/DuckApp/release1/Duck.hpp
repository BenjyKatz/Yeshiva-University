#include <iostream>

using namespace std;
namespace DuckSim{
    class Duck{
        public:
            virtual void quack();
            void swim();
            void fly();
            virtual void display()=0;
            //virtual ~Duck(){}
    };
}