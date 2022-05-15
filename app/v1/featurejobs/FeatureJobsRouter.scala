package v1.featurejobs

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.Inject

/**
  * Routes and URLs to the FeatureJobResource controller.
  */
class FeatureJobsRouter @Inject()(controller: FeatureJobsController) extends SimpleRouter {
  val prefix = "/v1/featurejobs"

  def link(id: FeatureJobsId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)
  }

}
