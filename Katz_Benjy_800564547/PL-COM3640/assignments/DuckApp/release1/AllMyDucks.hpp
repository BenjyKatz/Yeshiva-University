#include "Duck.hpp"
using namespace DuckSim;

    class MarbledDuck: public Duck{
        public: 
            void display() override;
            // void quack() ;
            // void fly() ;
            // void swim() ;
          //  ~MarbledDuck();
    };
    class WhitePekinDuck: public Duck{
        public:
            void display() override;
            void quack() override;
            // void fly() ;
            // void swim() ;
          //  ~WhitePekinDuck();
    };
