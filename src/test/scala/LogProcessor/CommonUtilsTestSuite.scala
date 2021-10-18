package LogProcessor

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import HelperUtils.{CommonUtils, ObtainConfigReference, Constants}
import org.apache.hadoop.conf.Configuration

/**
 * <h1>CommonUtilsTestSuite</h1>
 * <p>Collection of tests performed on CommonUtil Class.</p>
 *
 */
class CommonUtilsTestSuite extends AnyFlatSpec with Matchers {
  behavior of "CommonUtils Test Suite"
  // Get Default Configurations necessary for Program execution
  val config = ObtainConfigReference(Constants.LOG_PROCESSING) match {
    case Some(value) => value.getConfig(Constants.LOG_PROCESSING)
    case None => throw new RuntimeException("Cannot obtain a reference to the LogProcessing config data.")
  }
  // Get customizable configurations
  val userConfig = ObtainConfigReference(Constants.USER_INPUT) match {
    case Some(value) => value.getConfig(Constants.USER_INPUT)
    case None => throw new RuntimeException("Cannot obtain a reference to the userInput config data.")
  }

  it should "Create Configuration Object" in {
    val args: Array[String] = Array("input/path", "output/path", Constants.LONGEST_PATTERN_REGEX_MR)
    val conf = CommonUtils.getConfiguration(args)
    assert(conf.isInstanceOf[Configuration])
  }

  it should "Set default pattern for invalid pattern argument" in {
    val args: Array[String] = Array("input/path", "output/path", Constants.LONGEST_PATTERN_REGEX_MR)
    val conf = CommonUtils.getConfiguration(args)
    assert(conf.get(Constants.PATTERN) == Constants.DEFAULT_PATTERN)
  }

  it should "Set pattern1 as User Search Pattern" in {
    val pattern = Constants.PATTERN + "1"
    val args: Array[String] = Array("input/path", "output/path", Constants.LOG_FREQUENCY_DIST_MR, pattern)
    val conf = CommonUtils.getConfiguration(args)
    assert(conf.get(Constants.PATTERN) == userConfig.getConfig(Constants.LOGSEARCH).getString(pattern))
  }

}
