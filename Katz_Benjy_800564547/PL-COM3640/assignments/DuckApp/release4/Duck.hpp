#include <iostream>
#include "Behaviors.hpp"
using namespace std;

namespace DuckSim{

    class Duck {
        int id;
        static int nextID;
        public:
            Duck();
            FlyBehavior *Fb;
            QuackBehavior *Qb;
            virtual void performQuack();
            void swim();
            virtual void performFly();
            virtual void display()=0;
            virtual void setFlyBehavior(FlyBehavior *fb);
            virtual void setQuackBehavior(QuackBehavior *qb);
            virtual ~Duck(){
                nextID--;

            }
            virtual int getIdentifier();
    };
}