package v1.featurejobs

import javax.inject.{Inject, Provider}

import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

/**
 * DTO for displaying post information.
 */
case class PostResource(id: String, link: String, title: String, body: String)

object PostResource {
  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: Format[PostResource] = Json.format
}


/**
 * Controls access to the backend data, returning [[PostResource]]
 */
class FeatureJobsResourceHandler @Inject()(
                                     routerProvider: Provider[FeatureJobsRouter],
                                     postRepository: FeatureJobsRepository)(implicit ec: ExecutionContext) {

  def create(postInput: FeatureJobsFormInput)(
    implicit mc: MarkerContext): Future[PostResource] = {
    val data = FeatureJobsData(FeatureJobsId("999"), postInput.title, postInput.body)
    // We don't actually create the post, so return what we have
    postRepository.create(data).map { id =>
      createPostResource(data)
    }
  }

  def lookup(id: String)(
    implicit mc: MarkerContext): Future[Option[PostResource]] = {
    val postFuture = postRepository.get(FeatureJobsId(id))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createPostResource(postData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[PostResource]] = {
    postRepository.list().map { postDataList =>
      postDataList.map(postData => createPostResource(postData))
    }
  }

  private def createPostResource(p: FeatureJobsData): PostResource = {
    PostResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
  }

}
