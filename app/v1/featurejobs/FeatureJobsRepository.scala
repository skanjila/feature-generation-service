package v1.featurejobs

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

final case class FeatureJobsData(id: FeatureJobsId, title: String, body: String)

class FeatureJobsId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object FeatureJobsId {
  def apply(raw: String): FeatureJobsId = {
    require(raw != null)
    new FeatureJobsId(Integer.parseInt(raw))
  }
}

class FeatureJobsExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the FeatureJobRepository.
  */
trait FeatureJobsRepository {
  def create(data: FeatureJobsData)(implicit mc: MarkerContext): Future[FeatureJobsId]

  def list()(implicit mc: MarkerContext): Future[Iterable[FeatureJobsData]]

  def get(id: FeatureJobsId)(implicit mc: MarkerContext): Future[Option[FeatureJobsData]]
}

/**
  * A trivial implementation for the FeatureJob Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class FeatureJobsRepositoryImpl @Inject()()(implicit ec: FeatureJobsExecutionContext)
    extends FeatureJobsRepository {

  private val logger = Logger(this.getClass)

  private val FeatureJobsList = List(
    FeatureJobsData(FeatureJobsId("1"), "title 1", "blog FeatureJob 1"),
    FeatureJobsData(FeatureJobsId("2"), "title 2", "blog FeatureJob 2"),
    FeatureJobsData(FeatureJobsId("3"), "title 3", "blog FeatureJob 3"),
    FeatureJobsData(FeatureJobsId("4"), "title 4", "blog FeatureJob 4"),
    FeatureJobsData(FeatureJobsId("5"), "title 5", "blog FeatureJob 5")
  )

  override def list()(
      implicit mc: MarkerContext): Future[Iterable[FeatureJobsData]] = {
    Future {
      logger.trace(s"list: ")
      FeatureJobsList
    }
  }

  override def get(id: FeatureJobsId)(
      implicit mc: MarkerContext): Future[Option[FeatureJobsData]] = {
    Future {
      logger.trace(s"get: id = $id")
      FeatureJobsList.find(FeatureJob => FeatureJob.id == id)
    }
  }

  def create(data: FeatureJobsData)(implicit mc: MarkerContext): Future[FeatureJobsId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }

}
