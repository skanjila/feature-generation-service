import javax.inject._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import v1.featurejobs.{FeatureJobsRepository, FeatureJobsRepositoryImpl}

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure() = {
    bind[FeatureJobsRepository].to[FeatureJobsRepositoryImpl].in[Singleton]
  }
}
