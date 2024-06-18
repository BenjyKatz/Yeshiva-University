#include <iostream>
#include "AllMyDucks.hpp"

using namespace std;
using namespace DuckSim;
    MarbledDuck::MarbledDuck() {
        MarbledDuck(new FlyWithWings, new Quack);
    }
    MarbledDuck::MarbledDuck(FlyBehavior *fb, QuackBehavior *qb){
        MarbledDuck::MDFB = fb;
        MarbledDuck::MDQB = qb;
    }
    void MarbledDuck::performFly() {
        MarbledDuck::MDFB->fly();
    }
    void MarbledDuck::performQuack(){
        MarbledDuck::MDQB->quack();
    }
    void MarbledDuck::display() /*override*/{
        cout<<"Displaying a Marbled duck on the screen\n";
    }
    void MarbledDuck::setFlyBehavior(FlyBehavior *fb) {
        MarbledDuck::MDFB = fb;
    }
    void MarbledDuck::setQuackBehavior(QuackBehavior *qb){
        MarbledDuck::MDQB = qb;
    }
    MarbledDuck::~MarbledDuck(){

    }


    WhitePekinDuck::WhitePekinDuck() {
        WhitePekinDuck(new FlyWithWings, new QuackQuack);
    }
    WhitePekinDuck::WhitePekinDuck(FlyBehavior *fb, QuackBehavior *qb){
        WhitePekinDuck::Fb = fb;
        WhitePekinDuck::Qb = qb;
    }
    void WhitePekinDuck::display() /*override*/{
        cout<<"Displaying a White Pekin duck on the screen\n";
    }
    void WhitePekinDuck::performFly() {
        WhitePekinDuck::Fb->fly();
    }
    void WhitePekinDuck::performQuack() /*override*/{
        WhitePekinDuck::Qb->quack();
    }
    void WhitePekinDuck::setFlyBehavior(FlyBehavior *fb) {
        WhitePekinDuck::Fb = fb;
    }
    void WhitePekinDuck::setQuackBehavior(QuackBehavior *qb){
        WhitePekinDuck::Qb = qb;
    }
    WhitePekinDuck::~WhitePekinDuck()  {
    }

    RubberDuck::RubberDuck() {
        RubberDuck(new FlyNoWay, new Squeak);
    }
    RubberDuck::RubberDuck(FlyBehavior *fb, QuackBehavior *qb){
        RubberDuck::Fb = fb;
        RubberDuck::Qb = qb;
    }
    void RubberDuck::display() {
        cout<<"Displaying a rubber duck on the screen\n";
    }
    void RubberDuck::performQuack() {
        RubberDuck::Qb->quack();
    }
    void RubberDuck::performFly() {
        RubberDuck::Fb->fly();
    }
    void RubberDuck::setFlyBehavior(FlyBehavior *fb) {
        RubberDuck::Fb = fb;
    }
    void RubberDuck::setQuackBehavior(QuackBehavior *qb){
        RubberDuck::Qb = qb;
    }
    RubberDuck::~RubberDuck() {

    }

    DecoyDuck::DecoyDuck() {
        DecoyDuck(new FlyNoWay, new MuteQuack);
    }
    DecoyDuck::DecoyDuck(FlyBehavior *fb, QuackBehavior *qb){
        DecoyDuck::Fb = fb;
        DecoyDuck::Qb = qb;
    }

    void DecoyDuck::display() {
        cout<<"Displaying a decoy duck on the screen\n";
    }
    void DecoyDuck::performQuack() {
        DecoyDuck::Qb->quack();
    }
    void DecoyDuck::performFly() {
        DecoyDuck::Fb->fly();
    }
    void DecoyDuck::setFlyBehavior(FlyBehavior *fb) {
        DecoyDuck::Fb = fb;
    }
    void DecoyDuck::setQuackBehavior(QuackBehavior *qb){
        DecoyDuck::Qb = qb;
    }
    DecoyDuck::~DecoyDuck(){
        delete DecoyDuck::Qb;
        delete DecoyDuck::Fb;
    }