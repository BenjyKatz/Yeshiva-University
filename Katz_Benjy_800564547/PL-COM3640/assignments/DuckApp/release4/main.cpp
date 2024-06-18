#include "AllMyDucks.hpp"
#include <iostream>

using namespace std;
using namespace DuckSim;

void simulateDuck(Duck &duck) {
    cout << duck.getIdentifier() << " -------------------" << endl;
    duck.performQuack();
    duck.swim();
    duck.performFly();
    duck.display();
}

int main() {
    Duck *ducks[] = {new MarbledDuck,
                     new RubberDuck(new FlyWithWings(3), new Squeak)};
    for (auto duck: ducks) {
        simulateDuck(*duck);
        delete duck;
    }
}
/*
1 -------------------
quack!
Look, I am swimming
I can fly
Displaying a Marbled duck on the screen
2 -------------------
squeak
Look, I am swimming
I can fly fly fly
Displaying a rubber duck on the screen
*/
