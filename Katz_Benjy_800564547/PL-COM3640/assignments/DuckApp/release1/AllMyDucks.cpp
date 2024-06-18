#include <iostream>
#include "AllMyDucks.hpp"

using namespace std;
using namespace DuckSim;

    void MarbledDuck::display() /*override*/{
        cout<<"Displaying a Marbled duck on screen\n";
    }

    void WhitePekinDuck::display() /*override*/{
        cout<<"Displaying a White Pekin duck on screen\n";
    }
    void WhitePekinDuck::quack() /*override*/{
        cout<<"quack, quack!\n";
    }
            
