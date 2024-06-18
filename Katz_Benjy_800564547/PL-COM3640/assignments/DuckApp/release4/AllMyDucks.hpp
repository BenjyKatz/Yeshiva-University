#include "Duck.hpp"

using namespace DuckSim;

    class MarbledDuck: public Duck{
        public:
            FlyBehavior *MDFB = new FlyWithWings;
            QuackBehavior *MDQB = new Quack;
            MarbledDuck();
            MarbledDuck(FlyBehavior *fb, QuackBehavior *qb);
            void display() override;
            void performQuack() override;
            void performFly() override;
            void setFlyBehavior(FlyBehavior *fb) override;
            void setQuackBehavior(QuackBehavior *qb) override;
            // void swim() ;
            ~MarbledDuck();
    };
    class WhitePekinDuck: public Duck{
        public:
            FlyBehavior *Fb = new FlyWithWings;
            QuackBehavior *Qb = new QuackQuack;
            WhitePekinDuck();
            WhitePekinDuck(FlyBehavior *fb, QuackBehavior *qb);
            void display() override;
            void performQuack() override;
            void performFly() override;
            void setFlyBehavior(FlyBehavior *fb) override;
            void setQuackBehavior(QuackBehavior *qb) override;
            // void swim() ;
            ~WhitePekinDuck();
    };
    class RubberDuck: public Duck{
    public:
        FlyBehavior *Fb = new FlyNoWay;
        QuackBehavior *Qb = new Squeak;
        RubberDuck();
        RubberDuck(FlyBehavior *fb, QuackBehavior *qb);
        void display() override;
        void performQuack() override;
        void performFly() override;
        void setFlyBehavior(FlyBehavior *fb) override;
        void setQuackBehavior(QuackBehavior *qb) override;
        ~RubberDuck();
    };
    class DecoyDuck: public Duck{
    public:
        FlyBehavior *Fb = new FlyNoWay;
        QuackBehavior *Qb = new MuteQuack;
        DecoyDuck();
        DecoyDuck(FlyBehavior *fb, QuackBehavior *qb);
        void display() override;
        void performQuack() override;
        void performFly() override;
        void setFlyBehavior(FlyBehavior *fb) override;
        void setQuackBehavior(QuackBehavior *qb) override;
        ~DecoyDuck();
    };
