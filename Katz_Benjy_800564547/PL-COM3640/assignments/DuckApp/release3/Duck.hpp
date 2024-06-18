/*
 * If we wanted a duck to be able to quack in multiple ways, we could simply have the duck have a second constructor
 * that takes an array of QuackBehaviors and change performQuack to run quack() on every quack behavior
 */
#include <iostream>
#include "Behaviors.hpp"
using namespace std;
namespace DuckSim{
    class Duck {
        public:
            FlyBehavior *Fb;
            QuackBehavior *Qb;
            virtual void performQuack();
            void swim();
            virtual void performFly();
            virtual void display()=0;
            virtual void setFlyBehavior(FlyBehavior *fb);
            virtual void setQuackBehavior(QuackBehavior *qb);
            virtual ~Duck(){}
    };
}