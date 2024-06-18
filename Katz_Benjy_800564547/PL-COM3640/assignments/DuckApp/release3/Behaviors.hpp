

#ifndef RELEASE3_BEHAVIORS_HPP
#define RELEASE3_BEHAVIORS_HPP

#endif //RELEASE3_BEHAVIORS_HPP


    class FlyBehavior {
    public:
        virtual void fly();
    };

    class QuackBehavior {
    public:
        virtual void quack();
    };
    class FlyWithWings: public FlyBehavior{
    public:
        virtual void fly() override;
    };
    class FlyNoWay: public FlyBehavior{
    public:
        virtual void fly() override;
    };
    class Quack: public QuackBehavior{
    public:
        virtual void quack() override;
    };
    class Squeak: public QuackBehavior{
    public:
        virtual void quack() override;
    };
    class MuteQuack: public QuackBehavior{
    public:
        virtual void quack() override;
    };
    class QuackQuack: public QuackBehavior{
    public:
        virtual void quack() override;
    };