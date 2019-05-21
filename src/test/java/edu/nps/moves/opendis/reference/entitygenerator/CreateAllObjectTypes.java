package edu.nps.moves.opendis.reference.entitygenerator;

/**
 * CreateAllObjectTypes()); created on May 20, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@edu.nps.edu
 * @version $Id$
 */
import edu.nps.moves.dis.ObjectType;

public class CreateAllObjectTypes
{
    public CreateAllObjectTypes()
    {
    }

    public void run()
    {
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.GroundBurst.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.GroundBurst.Artillery());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.AirBurst.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.AirBurst.Grenade());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.TacticalSmoke_Canister.M83_White());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.TacticalSmoke_Canister.M18_Green());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.TacticalSmoke_Canister.M18_Violet());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.TacticalSmoke_Canister.M18_Yellow());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Tacticalsmoke.TacticalSmoke_Canister.M18_Red());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Abatis.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Abatis._8Tree());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Abatis._14Tree());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.LogCrib.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.LogCrib.Rectangular());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.LogCrib.Triangular());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Crater.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Crater.Small());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Crater.Medium());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Crater.Large());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.DragonsTeeth());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_HESCOBasket_Small());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_HESCOBasket_Medium());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_HESCOBasket_Large());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_HESCOBasket_Double_Stacked());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_Construction());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_Jersey_Plastic());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_Fence_Chain_6_foot());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_Fence_Wood_6_foot());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.Barrier.Barrier_Texas());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.RockDrop.RockDrop_Covered());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.RockDrop.RockDrop_Uncovered());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstacle.PotHole());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.VehicleDefilade.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.VehicleDefilade.ArmoredVehicle());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.VehicleDefilade.FightingVehicle());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.VehicleDefilade.MortarCarrier());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.VehicleDefilade.Tank());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.InfantryFightingPosition.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.InfantryFightingPosition.CoveredMachineGunBunker());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.InfantryFightingPosition.OverheadCoveredInfantryPosition());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.InfantryFightingPosition.Non_CoveredInfantryPosition());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.InfantryFightingPosition.Non_CoveredMachineGunBunker());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Preparedposition.InfantryFightingPosition.HastyFightingPosition());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.Church());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.ApartmentBuilding());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.GovernmentBuilding());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.IndustrialBuilding());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.Hanger());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.MicrowaveTower());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.PowerPylon());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.Radio_TVTower());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.School());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.TransformerYard());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.RadioTower_100ft());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.RadioTower_500ft());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.RadioTower_1000ft());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.PortaPotty());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.FOB_TrailerOffice());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.FOB_GuardTower());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.GuardHouse());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Building_Structure.Windmill());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.BuildingRubble.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.DisturbedEarth());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.DisturbedRoad.DisturbedRoad_Gravel());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.DisturbedRoad.DisturbedRoad_Asphalt());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.DisturbedRoad.DisturbedRoad_Concrete());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Small());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Medium());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Large());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_ModularGeneralPurposeTentSystemMGPTS());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Arctic());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_TEMPER());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_ExpandableFrame());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Fritsche());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Bedouin());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_ChemicallyandBiologicalProtectedShelterCBPS());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tent.Tent_Kuchi());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MaintenanceStructure.LightweightMaintenanceEnclosureLME_Bradley());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MaintenanceStructure.LightweightMaintenanceEnclosureLME_M1());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MaintenanceStructure.LargeAreaMaintenanceShelterLAMSVehicleMaintenanceVM());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MaintenanceStructure.LargeAreaMaintenanceShelterLAMSAviationMaintenanceAM());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_House());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_Hospital());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_GasStation());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_Store());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_OfficeBuilding());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_Warehouse());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_ControlTower());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_WaterTower());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_PoliceStation());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_FireStation());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_PowerStation());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_Apartment());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_School());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_Church());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_FarmHouse());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_MudbrickHouse());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.MOUTBuilding.MOUTBuilding_MudbrickStore());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_Store_Single());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_House_Single());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_House_Single_Railing());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_House_Double());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_Mosque_Single());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_Bridge_Single());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Container_BasedBuilding.Container_BasedBuilding_FOB());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tree_Deciduous.Tree_Deciduous_Small());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tree_Deciduous.Tree_Deciduous_Medium());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tree_Deciduous.Tree_Deciduous_Large());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tree_Evergreen.Tree_Evergreen_Small());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tree_Evergreen.Tree_Evergreen_Medium());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Tree_Evergreen.Tree_Evergreen_Large());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.Pump.Pump_Gas());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.IndustrialProcessingPlant.OilRefinery());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Culturalfeature.UtilityPole());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.StationaryBridge.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.StationaryBridge._2_Lane());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.StationaryBridge._4_Lane());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.AVLB.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.AVLB.M60A1());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.AVLB.MTU20());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.AVLB.JointAssaultBridgeJAB());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.RibbonBridge.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.RibbonBridge._2_Lane());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.RibbonBridge._4_Lane());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Passageway.Pier());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.Obstaclemarker.NBCHazardMarker());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.EnvironmentalObject.Flood.Flood_Small());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.EnvironmentalObject.Flood.Flood_Medium());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_PointObject.EnvironmentalObject.Flood.Flood_Large());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Tacticalsmoke.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Tacticalsmoke.ExhaustSmoke.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.TankDitch.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.ConcertinaWire.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.ConcertinaWire._2_Roll());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.ConcertinaWire._3_Roll());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.ConcreteBarrier());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.SpeedBump());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.Rut());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstacle.ChainLinkFence());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Culturalfeature.Wire.Wire_Crush());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Culturalfeature.Tracks_Tire());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstaclemarker.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstaclemarker.MinefieldLaneMarker.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstaclebreach.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_LinearObject.Obstaclebreach.Breach.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_ArealObject.Obstacle.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_ArealObject.Obstacle.Minefield.Other());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_ArealObject.Obstacle.Minefield.Hasty());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_ArealObject.Obstacle.Minefield.Prepared());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_ArealObject.Obstacle.Minefield.Scattered());
        dumpObjectType(new edu.nps.moves.dis.objecttypes.ObjectTypes_ArealObject.Obstacle.Minefield.Solitary());

    }
    
    String formatStr = "Name: %s\tDomain: %s\tKind: %s\tCategory: %s\tSubcategory: %s";
    private void dumpObjectType(ObjectType ot)
    {
        String dom = ot.getDomain().name();
        String kind = ot.getObjectKind().name();
        String cat = ot.getCategory().getDescription();
        edu.nps.moves.dis.SubCategory sub = ot.getSubcategory();
        String substr = (sub==null?"":sub.getDescription());
        String nm = ot.getClass().getName();
 
        System.out.println(String.format(formatStr, nm, dom, kind, cat, substr));
    }

    public static void main(String[] args)
    {
        new CreateAllObjectTypes().run();
    }
}
