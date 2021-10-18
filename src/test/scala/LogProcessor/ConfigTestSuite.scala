package LogProcessor

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import HelperUtils.{ObtainConfigReference, Constants}
import com.typesafe.config.{Config, ConfigFactory}

class ConfigTestSuite extends AnyFlatSpec with Matchers {
  behavior of "Configuration parameters module"
  // App Config Tests
  var config = ObtainConfigReference(Constants.LOG_PROCESSING) match {
    case Some(value) => value.getConfig(Constants.LOG_PROCESSING)
    case None => throw new RuntimeException("Cannot obtain reference to the Config data")
  }

  it should "contain Log Regex Pattern" in {
    assert(config.hasPath(Constants.LOG_REGEX_PATTERN))
  }

  it should "contain Hour Window Pattern" in {
    assert(config.hasPath(Constants.HOUR_WINDOW_PATTRN))
  }

  it should "contain TimeStamp pattern" in {
    assert(config.hasPath(Constants.TIMESTAMP_PATTERN))
  }

  it should "contain file name pattern" in {
    assert(config.hasPath(Constants.FILE_NAME_PATTERN))
  }

  it should "contain logString_group" in {
    assert(config.hasPath(Constants.LOGSTR))
  }

  it should "contain classname group" in {
    assert(config.hasPath(Constants.CLASSNAME))
  }

  // User Config Tests
  var userConfig = ObtainConfigReference(Constants.USER_INPUT) match {
    case Some(value) => value.getConfig(Constants.USER_INPUT)
    case None => throw new RuntimeException("Cannot obtain reference to the Config data")
  }
  it should "contain time interval" in {
    assert(userConfig.hasPath(Constants.TIMEINTERVAL))
  }
  it should "contain logSearch pattern" in {
    assert(userConfig.hasPath(Constants.LOGSEARCH))
  }
  it should "contain default regex pattern" in {
    val logSearch: Config = userConfig.getConfig(Constants.LOGSEARCH)
    assert(logSearch.hasPath(Constants.PATTERN + "1"))
  }

}
