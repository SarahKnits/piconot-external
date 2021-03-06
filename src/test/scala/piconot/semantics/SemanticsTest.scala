package piconot.semantics

import org.scalatest.FunSpec
import picolib.semantics._
import piconot.ir._
import edu.hmc.langtools._
import piconot.parser.PiconotParser

class SemanticsTest extends FunSpec with LangInterpretMatchers[AST,List[Rule]] {
  override val parser = PiconotParser.apply _
  override val interpreter = eval _

  describe("A rule") {
    listRules = List()
    it("can parse a simple command with one option") {
      program("If you are on First St. and can go uptown, go uptown on First St.") should compute(
        List(Rule(State("0"),
          Surroundings(Open, Anything, Anything, Anything),
          North,
          State("0"))))
    }
  }

  describe("Another rule") {
    it("can parse a simple command with different states") {
      program("If you are on First St. and can go uptown, go uptown on Second St.") should compute(
        List(Rule(State("0"),
          Surroundings(Open, Anything, Anything, Anything),
          North,
          State("0")),
        Rule(State("0"),
          Surroundings(Open, Anything, Anything, Anything),
          North,
          State("1"))))
    }

    it("can parse a simple command with different states part 2") {
      program("If you are on Second St. and can go uptown, go uptown on First St.") should compute(
        List(Rule(State("0"),
          Surroundings(Open, Anything, Anything, Anything),
          North,
          State("0")),
          Rule(State("0"),
            Surroundings(Open, Anything, Anything, Anything),
            North,
            State("1")),
          Rule(State("1"),
            Surroundings(Open, Anything, Anything, Anything),
            North,
            State("0"))))
    }

    it("can parse a simple command with staying") {
      program("If you are on First St. and can go uptown, teleport to First St.") should compute(
        List(Rule(State("0"),
          Surroundings(Open, Anything, Anything, Anything),
          North,
          State("0")),
          Rule(State("0"),
            Surroundings(Open, Anything, Anything, Anything),
            North,
            State("1")),
          Rule(State("1"),
            Surroundings(Open, Anything, Anything, Anything),
            North,
            State("0")),
          Rule(State("0"),
            Surroundings(Open, Anything, Anything, Anything),
            StayHere,
            State("0"))))
    }

    it("can parse a command with four options") {
      program("If you are on First St. and can go uptown and cannot go downtown and cannot go into town and can go outta town, go uptown on First St.") should compute(
        List(Rule(State("0"),
          Surroundings(Open, Anything, Anything, Anything),
          North,
          State("0")),
          Rule(State("0"),
            Surroundings(Open, Anything, Anything, Anything),
            North,
            State("1")),
          Rule(State("1"),
            Surroundings(Open, Anything, Anything, Anything),
            North,
            State("0")),
          Rule(State("0"),
            Surroundings(Open, Anything, Anything, Anything),
            StayHere,
            State("0")),
          Rule(State("0"),
            Surroundings(Open, Open, Blocked, Blocked),
            North,
            State("0")))
      )
    }
  }
}

class SemanticsTestTwo extends FunSpec with LangInterpretMatchers[AST,List[Rule]] {
  override val parser = PiconotParser.apply _
  override val interpreter = eval _

  describe("Programs") {
    it("can be parsed as multiple commands") {
      program("If you are on Second St. and can go downtown, go downtown on Fourth St." +
        "If you are on Second St. and cannot go downtown and can go outta town, go outta town on Second St." +
        "If you are on Second St. and cannot go downtown and cannot go outta town and can go uptown, go uptown on Main St." +
        "If you are on Second St. and cannot go downtown and cannot go outta town and cannot go uptown and can go into town, go into town on Third St.") should compute(
        List(Rule(State("0"),
          Surroundings(Anything, Anything, Anything, Open),
          South,
          State("1")),
          Rule(State("0"),
            Surroundings(Anything, Open, Anything, Blocked),
            East,
            State("0")),
          Rule(State("0"),
            Surroundings(Open, Blocked, Anything, Blocked),
            North,
            State("2")),
          Rule(State("0"),
            Surroundings(Blocked, Blocked, Open, Blocked),
            West,
            State("3")))
      )
    }
  }
}