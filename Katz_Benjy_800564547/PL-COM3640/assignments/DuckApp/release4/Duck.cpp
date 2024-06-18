#include <iostream>
#include "Duck.hpp"
using namespace std;
using namespace DuckSim;
    int Duck::nextID = 0;
    Duck::Duck(){
        id = ++nextID;
    }
    int Duck::getIdentifier(){
        return id;
    }
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

