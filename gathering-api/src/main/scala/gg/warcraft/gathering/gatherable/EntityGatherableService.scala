/*
 * MIT License
 *
 * Copyright (c) 2020 WarCraft <https://github.com/WarCraft>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.warcraft.gathering.gatherable

import gg.warcraft.gathering.EntityGatheringSpot
import gg.warcraft.monolith.api.core.Duration._
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.core.task.TaskService
import gg.warcraft.monolith.api.entity.{Entity, EntityService}
import gg.warcraft.monolith.api.item.ItemService
import gg.warcraft.monolith.api.math.Vector3f
import gg.warcraft.monolith.api.player.Player

import scala.util.Random

class EntityGatherableService(implicit
    eventService: EventService,
    taskService: TaskService,
    entityService: EntityService,
    itemService: ItemService
) extends GatherableService {
  protected final val dropOffset = Vector3f(0, 0.5f, 0)

  def gatherEntity(
      spot: EntityGatheringSpot,
      gatherable: EntityGatherable,
      entity: Entity,
      player: Player
  ): Boolean = {
    var preGatherEvent = EntityPreGatherEvent(entity, spot, player)
    preGatherEvent = eventService.publish(preGatherEvent)
    if (preGatherEvent.allowed) {
      spawnDrops(gatherable, entity.location)
      spot.entityIds -= entity.id

      val gatherEvent = EntityGatherEvent(entity, spot, player)
      eventService.publish(gatherEvent)
      true
    } else false
  }

  def queueEntityRespawn(
      gatherable: EntityGatherable,
      spot: EntityGatheringSpot
  ): Unit = {
    val cooldown = gatherable.cooldown + Random.nextInt(gatherable.cooldownDelta)
    taskService.evalLater(
      cooldown.seconds, {
        val entityId = entityService.spawnEntity(gatherable.entityType, spot.spawn)
        spot.entityIds += entityId

        // NOTE option to publish GatherableEntityRespawnEvent
      }
    )
  }
}
