#include <iostream>
#include "Behaviors.hpp"
using namespace std;
namespace DuckSim{
    class Duck: public QuackBehavior, public FlyBehavior{
        public:
            virtual void performQuack();
            void swim();
            virtual void performFly();
            virtual void display()=0;
            //virtual ~Duck(){}
    };
}