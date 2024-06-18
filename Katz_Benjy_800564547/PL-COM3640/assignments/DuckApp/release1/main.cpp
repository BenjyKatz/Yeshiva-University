//#include "Duck.hpp"


#include "AllMyDucks.hpp"
#include <iostream>

using namespace std;
using namespace DuckSim;

void simulateDuck(Duck &duck) {
    cout << "-------------------" << endl;
    duck.quack();
    duck.swim();
    duck.fly();
    duck.display();
}

int main() {

    Duck *ducks[] = {new MarbledDuck, new WhitePekinDuck};
    for (auto duck: ducks) {
        simulateDuck(*duck);
        delete duck;
    }
    
}

/*
-------------------
quack!
Look, I am swimming
I can fly
Displaying a Marbled duck on the screen
-------------------
quack, quack!
Look, I am swimming
I can fly
Displaying a White Pekin duck on the screen
 */
