/*
 * A lot of code ends up being repeated because While there is a standard behavoir we
 * do not chose to enforce it because of the exceptions. Ideally we should set the default as
 * the standard behavior and only make the client with the unstandard behavior. It would be much simpler to have one
 * standard method that duck subclasses inherit and any non unique duck can be adjusted in one fell swoop.
 *
 * The issue would be about the same if it was done using a java interface because in both casses all methods need to be
 * overwritten if they did not follow the standard
 */
#include <iostream>
#include "AllMyDucks.hpp"

using namespace std;
using namespace DuckSim;

    void MarbledDuck::display() /*override*/{
        cout<<"Displaying a Marbled duck on screen\n";
    }
    void MarbledDuck::performFly() {
        fly();
    }
    void MarbledDuck::performQuack(){
        quack();
    }

    void WhitePekinDuck::display() /*override*/{
        cout<<"Displaying a White Pekin duck on screen\n";
    }
    void WhitePekinDuck::performFly() {
        fly();
    }
    void WhitePekinDuck::performQuack() /*override*/{
        cout<<"quack, quack!\n";
    }

    void RubberDuck::display() {
        cout<<"Displaying a rubber duck on the screen\n";
    }
    void RubberDuck::performQuack() {
        cout<<"squeak\n";
    }

    void DecoyDuck::display() {
        cout<<"Displaying a decoy duck on the screen\n";
    }
            
