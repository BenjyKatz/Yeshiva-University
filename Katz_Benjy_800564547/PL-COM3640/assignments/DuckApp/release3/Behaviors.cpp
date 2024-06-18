
#include "Behaviors.hpp"
#include <iostream>
using namespace std;

void FlyBehavior::fly() {

}
void QuackBehavior::quack() {

}
void FlyWithWings::fly(){
    cout<<"I can fly\n";
}
void FlyNoWay::fly(){

}
void Quack::quack(){
    cout<<"quack!\n";
}
void QuackQuack::quack(){
    cout<<"quack, quack!\n";
}
void Squeak::quack(){
    cout<<"squeak\n";
}
void MuteQuack::quack(){

}