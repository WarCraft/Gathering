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

package gg.warcraft.gathering

import gg.warcraft.gathering.gatherable.EntityGatherableService
import gg.warcraft.monolith.api.entity.EntityService

class GatheringSpotService(implicit
    entityService: EntityService,
    entityGatherableService: EntityGatherableService
) {
  private var _gatheringSpots: List[GatheringSpot] = Nil
  def gatheringSpots: List[GatheringSpot] = _gatheringSpots

  def readConfig(config: GatheringConfig): Unit =
    config.gatheringSpots.foreach(addGatheringSpot)

  private def initGatheringSpot(spot: GatheringSpot): Unit =
    spot.entities.foreach { entity =>
      for (_ <- 1 to entity.entityCount)
        entityGatherableService.queueEntityRespawn(entity, spot)

      entityService.getEntitiesWithin(spot.boundingBox)
        .filter { _.typed == entity.entityType }
        .filter { it => !spot.entityIds.contains(it.id) }
        .foreach(it => entityService.removeEntity(it.id))
    }

  def addGatheringSpot(spot: GatheringSpot): Boolean =
    if (!_gatheringSpots.exists { _.id == spot.id }) {
      _gatheringSpots ::= spot
      initGatheringSpot(spot)
      true
    } else false

  def removeGatheringSpot(id: String): Boolean =
    _gatheringSpots.find { _.id == id } match {
      case Some(spot) =>
        _gatheringSpots = _gatheringSpots.filter { _.id != id }
        spot.entityIds.foreach(entityService.removeEntity)
        true
      case None => false
    }
}
