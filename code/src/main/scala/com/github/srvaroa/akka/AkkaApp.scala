package com.github.srvaroa.akka

import akka.actor._
import akka.util._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.util.concurrent.atomic._

case object Request

class Worker(val stopAt: Int, processed: AtomicInteger) extends Actor {
    var count = 0
    val batch = 10000

    def receive = {
        case Request =>
            count +=1
            if (count % batch == 0) {
                println(s"${self.path.name} ${processed.get()} / $stopAt")
                if (processed.addAndGet(batch) == stopAt) {
                    println("Shutting down")
                    context.system.terminate()
                }
            }
    }
}

class AkkaRun {
    def run() {
        var requests = 1000 * 1000
        val processed = new AtomicInteger(0)

        val system = ActorSystem("TrivialSystem")
        val worker1 = system.actorOf(Props(classOf[Worker], requests, processed), "Worker1")
        val worker2 = system.actorOf(Props(classOf[Worker], requests, processed), "Worker2")

        while (requests > 0) {
            worker1 ! Request
            worker2 ! Request
            requests -=2
        }

        val timeout = Timeout(60 seconds)
        Await.result(system.whenTerminated, timeout.duration)
    }
}
 
object AkkaApp extends App {
    val instance = new AkkaRun()
    instance.run()
}
