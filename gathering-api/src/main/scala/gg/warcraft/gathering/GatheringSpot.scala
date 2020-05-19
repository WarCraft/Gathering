package gg.warcraft.gathering

import java.util.UUID

import gg.warcraft.gathering.gatherable.{BlockGatherable, EntityGatherable}
import gg.warcraft.monolith.api.block.box.BlockBox
import gg.warcraft.monolith.api.block.Block

import scala.collection.mutable

class GatheringSpot(
    val id: String,
    val boundingBox: BlockBox,
    val blockGatherables: List[BlockGatherable],
    val entityGatherables: List[EntityGatherable]
) {
  // TODO initialize entities from repository
  val entities: mutable.Set[UUID] = mutable.Set()

  def contains(block: Block): Boolean =
    boundingBox.test(block.location)

  def contains(entityId: UUID): Boolean =
    entities.contains(entityId)
}