#include "Duck.hpp"

using namespace DuckSim;

    class MarbledDuck: public Duck{
        public: 
            void display() override;
            void performQuack() override;
            void performFly() override;
            // void swim() ;
          //  ~MarbledDuck();
    };
    class WhitePekinDuck: public Duck{
        public:
            void display() override;
            void performQuack() override;
            void performFly() override;
            // void swim() ;
          //  ~WhitePekinDuck();
    };
    class RubberDuck: public Duck{
    public:
        void display() override;
        void performQuack() override;
    };
    class DecoyDuck: public Duck{
    public:
        void display() override;
    };
