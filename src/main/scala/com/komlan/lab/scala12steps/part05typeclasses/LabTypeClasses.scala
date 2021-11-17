package com.komlan.lab.scala12steps.part05typeclasses

object LabTypeClasses {
    
    println("-" * 50)
    println(("--     Prefer Type Classes Over Interface       --" ))
    println("-" * 50)

    // Part 5.1: Implicit parameter
    lazy val part1 = {
        def multiply(number: Int)(implicit factor: Int): Int = number * factor
        println(multiply(3)(10))
        println(multiply(2)(10))
        def scoped(): Unit ={
            implicit val f: Int = 10
            println(multiply(20))
        }
        scoped()

        def limitedScope(): Unit = {
            implicit val n: Int = 20
            println(multiply(10))
        }
        
        limitedScope()
    }

    // Part 5.2: Implicit conversion
    lazy val part2: String = {
        def alert(msg: String): Unit = println(msg)

        alert("Cool")
        implicit def intToString(i: Int): String = i.toString
        alert(7)

        // Compiler will look for implicit conversion when code tries to access 
        // object member which is not defined

        // /// Option 1: implicit def
        // class LoquaciousInt(x: Int) {
        //     def chat: Unit = for (_ <- 1 to x) println("Hi")
        // }
        // implicit def intToLoquaciousInt(x: Int) = new LoquaciousInt(x)

        /// Option 2: implicit class - shorthand for option 1 (class with one-argument constructor can be marked implicit)
        // Often refered to as Type enrichment, similar to extension method in c#
        implicit class LoquaciousInt(x: Int) {
            def chat(): Unit = for (_ <- 1 to x) println("Hi")
        }
        3.chat() // error !!!
        "Part 2: End"
    }

    // Part 5.3 Type classes
    val part3: String = {

        trait CanChat[A] {
            def chat(x: A): String
        }

        // Life wintout type classes
        final case class Person(firstName: String, lastName: String)
        
        final case class Dog(name: String)
        object ChattyAddons {
            implicit object PersonCanChat extends CanChat[Person] {
                def chat(x: Person): String = s"Hi, I'm ${x.firstName}"
            }
            implicit object DogCanChat extends CanChat[Dog] {
                def chat(x: Dog): String = s"Woof, my name is ${x.name}"
            }
    
            implicit final class ChatUtil[A](x: A) {
                def chat(implicit makesChatty: CanChat[A]): String = makesChatty.chat(x)
            }
        }

        
        // println (PersonCanChat.chat(Person("Will", "Smith")))
        // println(DogCanChat.chat(Dog("Gobby")))

        import ChattyAddons._
        println(Dog("Gobby").chat)
        println(Person("Will", "Smith").chat)

       
        object PersonCanChatFormally extends CanChat[Person] {
            def chat(x: Person) = s"Hello, I'm ${x.firstName} ${x.lastName}"
        }

        println(Person("Jaydan", "Smith").chat(PersonCanChatFormally))


        "Part 3"
    }

    
    
    
}