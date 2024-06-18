#include <iostream>
#include "Duck.hpp"
using namespace std;
using namespace DuckSim;
    void Duck::setFlyBehavior(FlyBehavior *fb) {
        Duck::Fb = fb;
    }
    void Duck::setQuackBehavior(QuackBehavior *qb) {
        Duck::Qb = qb;
    }
    void Duck::performQuack(){

     //   cout<<"quack!\n";
    }
    void Duck::swim(){
        cout<<"Look, I am swimming\n";
    }
    void Duck::performFly(){
      //  cout<<"I can fly\n";
    }
    void Duck::display(){
    }
/*
    void Duck::performFly() {
        Duck::Fb->fly();
    }
    void Duck::performQuack() {
        Duck::Qb->quack();
    }
    void Duck::setFlyBehavior(FlyBehavior *fb) {
        Duck::Fb = fb;
    }
    void Duck::setQuackBehavior(QuackBehavior *qb){
        Duck::Qb = qb;
    }
    */