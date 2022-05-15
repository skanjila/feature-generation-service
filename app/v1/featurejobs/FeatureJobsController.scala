package v1.featurejobs

import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class FeatureJobsFormInput(title: String, body: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class FeatureJobsController @Inject()(cc: FeatureJobControllerComponents)(
    implicit ec: ExecutionContext)
    extends FeatureJobBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[FeatureJobsFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "title" -> nonEmptyText,
        "body" -> text
      )(FeatureJobsFormInput.apply)(FeatureJobsFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = FeatureJobsAction.async { implicit request =>
    logger.trace("index: ")
    featureJobsResourceHandler.find.map { FeatureJobs =>
      Ok(Json.toJson(FeatureJobs))
    }
  }

  def process: Action[AnyContent] = FeatureJobsAction.async { implicit request =>
    logger.trace("process: ")
    processJsonFeatureJob()
  }

  def show(id: String): Action[AnyContent] = FeatureJobsAction.async {
    implicit request =>
      logger.trace(s"show: id = $id")
      featureJobsResourceHandler.lookup(id).map { FeatureJobs =>
        Ok(Json.toJson(FeatureJobs))
      }
  }

  private def processJsonFeatureJob[A]()(
      implicit request: FeatureJobsRequest[A]): Future[Result] = {
    def failure(badForm: Form[FeatureJobsFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: FeatureJobsFormInput) = {
      featureJobsResourceHandler.create(input).map { featureJob =>
        Created(Json.toJson(featureJob)).withHeaders(LOCATION -> featureJob.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
