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

import gg.warcraft.gathering.gatherable.{BlockGatherable, EntityGatherable}
import gg.warcraft.monolith.api.block.Block
import gg.warcraft.monolith.api.block.box.BlockBox
import gg.warcraft.monolith.api.world.Location

import java.util.UUID

trait GatheringSpot {
  val id: GatheringSpot.Id
  val boundingBox: BlockBox
}

class BlockGatheringSpot(
    val id: GatheringSpot.Id,
    val boundingBox: BlockBox,
    val blocks: List[BlockGatherable]
) extends GatheringSpot {
  def contains(block: Block): Boolean = {
    val hasType = blocks.exists { it => block.hasData(it.blockData) }
    hasType && boundingBox.test(block.location)
  }
}

class EntityGatheringSpot(
    val id: GatheringSpot.Id,
    val boundingBox: BlockBox,
    val spawn: Location,
    val entities: List[EntityGatherable]
) extends GatheringSpot {
  var entityIds: Set[UUID] = Set.empty

  def contains(entityId: UUID): Boolean =
    entityIds.contains(entityId)
}

object GatheringSpot {
  type Id = String
}

object EntityGatheringSpot {
  case class Config(
      id: GatheringSpot.Id,
      boundingBox: BlockBox,
      spawn: Location,
      entities: List[EntityGatherable]
  ) {
    def parse(): EntityGatheringSpot =
      new EntityGatheringSpot(id, boundingBox, spawn, entities)
  }
}
