package gg.warcraft.gathering.app.spot.service;

import com.google.inject.Inject;
import gg.warcraft.gathering.api.spot.BlockGatheringSpot;
import gg.warcraft.gathering.api.spot.EntityGatheringSpot;
import gg.warcraft.gathering.api.spot.service.GatheringSpotQueryService;
import gg.warcraft.gathering.api.spot.service.GatheringSpotRepository;

import java.util.List;

public class DefaultGatheringSpotQueryService implements GatheringSpotQueryService {
    private final GatheringSpotRepository gatheringSpotRepository;

    @Inject
    public DefaultGatheringSpotQueryService(GatheringSpotRepository gatheringSpotRepository) {
        this.gatheringSpotRepository = gatheringSpotRepository;
    }

    @Override
    public List<BlockGatheringSpot> getBlockGatheringSpots() {
        return gatheringSpotRepository.getBlockGatheringSpots();
    }

    @Override
    public List<EntityGatheringSpot> getEntityGatheringSpots() {
        return gatheringSpotRepository.getEntityGatheringSpots();
    }
}
