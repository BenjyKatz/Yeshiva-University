
#include "Behaviors.hpp"
#include <iostream>
using namespace std;

void FlyBehavior::fly() {

}
void QuackBehavior::quack() {

}
FlyWithWings::FlyWithWings(int x) {
    if(x>5||x<1){
        throw std::out_of_range ("Speed out of range");
    }
    FlyWithWings::repeats = x;
}
FlyWithWings::FlyWithWings(){
    FlyWithWings(1);
}
void FlyWithWings::fly(){

    cout<<"I can ";
    for(int i =1; i<FlyWithWings::repeats; i++){
        cout<<"fly ";
    }
    cout<<"fly\n";
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