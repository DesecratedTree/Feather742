package com.feather.game;

import java.util.ArrayList;
import java.util.List;

import com.feather.cache.Cache;
import com.feather.game.item.Item;
import com.feather.game.npc.NPC;
import com.feather.game.player.Player;

public final class RegionBuilder {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	
	/*
	 * build here the maps you wont edit again
	 */
	public static void init() {

		for(int mapX = 0; mapX < MAX_REGION_X;  mapX++) {
			for(int mapY = 0; mapY < MAX_REGION_Y;  mapY++) {  
				if (Cache.STORE.getIndexes()[5].getArchiveId("m" + mapX + "_" + mapY) != -1) 
					EXISTING_MAPS.add(getRegionHash(mapX, mapY));
			}
		}
		World.getRegion(7503, true);
		World.getRegion(7759, true);
		spawnGroundItems();
		/*for(int i = 0; i < 2000; i++) {
			
			int[] boundChuncks = RegionBuilder.findEmptyChunkBound(
					8,
					8);
			// reserves all map area
			RegionBuilder.cutMap(boundChuncks[0], boundChuncks[1],
					8,
					8, 0);
			
			System.out.println(i+", "+Arrays.toString(boundChuncks));
		}*/
		
		
	}

	public static int getRegion(int c) {
		return c >> 3;
	}

	/*
	 * do not use this out builder
	 */
	public static void noclipCircle(int x, int y, int plane, int ratio)
			throws InterruptedException {
		for (int xn = x - ratio; xn < x + ratio; xn++) {
			for (int yn = y - ratio; yn < y + ratio; yn++) {
				if (Math.pow(2, x - xn) + Math.pow(2, y - yn) <= Math.pow(2,
						ratio)) {
					int regionId = new WorldTile(xn, yn, 0).getRegionId();
					Region region = World.getRegion(regionId);
					int baseLocalX = xn - ((regionId >> 8) * 64);
					int baseLocalY = yn - ((regionId & 0xff) * 64);
					while (region.getLoadMapStage() != 2) { // blocks waiting
															// for load of
															// region to be come
						// System.out.println("nocliping: "+xn+", "+yn);
						Thread.sleep(1);
					}
					System.out.println("nocliping: " + xn + ", " + yn + ", "
							+ baseLocalX + ", " + baseLocalY);
					System.out
							.println(region.forceGetRegionMap().getMasks()[plane][baseLocalX][baseLocalY]);
					region.forceGetRegionMap().setMask(plane, baseLocalX,
							baseLocalY, 0);
					System.out
							.println(region.forceGetRegionMap().getMasks()[plane][baseLocalX][baseLocalY]);

					region.forceGetRegionMapClipedOnly().setMask(plane,
							baseLocalX, baseLocalY, 0);
				}
			}
		}
	}


	
	
	private static final Object ALGORITHM_LOCK = new Object();
	
	private static final List<Integer> EXISTING_MAPS = new ArrayList<Integer>();
	
	
	private static final int MAX_REGION_X = 127;
	private static final int MAX_REGION_Y = 255;
	
	
	public static int[] findEmptyRegionBound(int widthChunks, int heightChunks) {
		int regionHash = findEmptyRegionHash(widthChunks, heightChunks);
		return new int[] {(regionHash >> 8), regionHash & 0xff};
	}
	
	public static int[] findEmptyChunkBound(int widthChunks, int heightChunks) {
		int[] map = findEmptyRegionBound(widthChunks, heightChunks);
		map[0] *= 8;
		map[1] *= 8;
		return map;
	}
	
	public static int getRegionHash(int chunkX, int chunkY) {
		return (chunkX << 8) + chunkY;
	}
	
	public static int findEmptyRegionHash(int widthChunks, int heightChunks) {
		int regionsDistanceX = 1;
		while(widthChunks > 8) {
			regionsDistanceX += 1;
			widthChunks -= 8;
		}
		int regionsDistanceY = 1;
		while(heightChunks > 8) {
			regionsDistanceY += 1;
			heightChunks -= 8;
		}
		synchronized (ALGORITHM_LOCK) {
			for(int regionX = 1; regionX <= MAX_REGION_X - regionsDistanceX;  regionX++) { 
				skip: for(int regionY = 1; regionY <= MAX_REGION_Y - regionsDistanceY;  regionY++) { 
					int regionHash = getRegionHash(regionX, regionY); //map hash because skiping to next map up		
					for(int checkRegionX = regionX - 1; checkRegionX <= regionX + regionsDistanceX; checkRegionX++) {
						for(int checkRegionY = regionY - 1; checkRegionY <= regionY + regionsDistanceY; checkRegionY++) {
							int hash = getRegionHash(checkRegionX, checkRegionY);
							if(regionExists(hash)) 
								continue skip;
							
						}
					}
					reserveArea(regionX, regionY, regionsDistanceX, regionsDistanceY, false);
					return regionHash;
				}
			}
		}
		return -1;
		
	}
	
	public static void reserveArea(int fromRegionX, int fromRegionY, int width, int height, boolean remove) {
		for (int regionX = fromRegionX; regionX < fromRegionX + width;  regionX++) {
			for (int regionY = fromRegionY; regionY < fromRegionY + height;  regionY++) {
				if(remove)
						EXISTING_MAPS.remove((Integer)getRegionHash(regionX, regionY));
					else
						EXISTING_MAPS.add(getRegionHash(regionX, regionY));
			}
		}
	}
	
	
	public static boolean regionExists(int mapHash) {	
		return EXISTING_MAPS.contains(mapHash);
		
	}

	public static void cutRegion(int chunkX, int chunkY, int plane) {
		DynamicRegion toRegion = createDynamicRegion((((chunkX / 8) << 8) + (chunkY / 8)));
		int offsetX = (chunkX - ((chunkX / 8) * 8));
		int offsetY = (chunkY - ((chunkY / 8) * 8));
		toRegion.getRegionCoords()[plane][offsetX][offsetY][0] = 0;
		toRegion.getRegionCoords()[plane][offsetX][offsetY][1] = 0;
		toRegion.getRegionCoords()[plane][offsetX][offsetY][2] = 0;
		toRegion.getRegionCoords()[plane][offsetX][offsetY][3] = 0;
	}

	
	public static final void destroyMap(int chunkX, int chunkY,
			int widthRegions, int heightRegions) {
		synchronized (ALGORITHM_LOCK) {
			int fromRegionX = chunkX / 8;
			int fromRegionY = chunkY / 8;
			int regionsDistanceX = 1;
			while(widthRegions > 8) {
				regionsDistanceX += 1;
				widthRegions -= 8;
			}
			int regionsDistanceY = 1;
			while(heightRegions > 8) {
				regionsDistanceY += 1;
				heightRegions -= 8;
			}
			for (int regionX = fromRegionX; regionX < fromRegionX + regionsDistanceX;  regionX++) {
				for (int regionY = fromRegionY; regionY < fromRegionY + regionsDistanceY;  regionY++) {
					destroyRegion(getRegionHash(regionX, regionY));
				}
			}
			reserveArea(fromRegionX, fromRegionY, regionsDistanceX, regionsDistanceY, true);
		}
	}

	public static final void repeatMap(int toChunkX, int toChunkY,
			int widthChunks, int heightChunks, int rx, int ry, int plane,
			int rotation, int... toPlanes) {
		for (int xOffset = 0; xOffset < widthChunks; xOffset++) {
			for (int yOffset = 0; yOffset < heightChunks; yOffset++) {
				int nextChunkX = toChunkX + xOffset;
				int nextChunkY = toChunkY + yOffset;
				DynamicRegion toRegion = createDynamicRegion((((nextChunkX / 8) << 8) + (nextChunkY / 8)));
				int regionOffsetX = (nextChunkX - ((nextChunkX / 8) * 8));
				int regionOffsetY = (nextChunkY - ((nextChunkY / 8) * 8));
				for (int pIndex = 0; pIndex < toPlanes.length; pIndex++) {
					int toPlane = toPlanes[pIndex];
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = rx;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = ry;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = plane;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][3] = rotation;
					World.getRegion((((rx / 8) << 8) + (ry / 8)), true);
				}
			}
		}
	}

	public static final void cutMap(int toChunkX, int toChunkY,
			int widthChunks, int heightChunks, int... toPlanes) {
		for (int xOffset = 0; xOffset < widthChunks; xOffset++) {
			for (int yOffset = 0; yOffset < heightChunks; yOffset++) {
				int nextChunkX = toChunkX + xOffset;
				int nextChunkY = toChunkY + yOffset;
				DynamicRegion toRegion = createDynamicRegion((((nextChunkX / 8) << 8) + (nextChunkY / 8)));
				int regionOffsetX = (nextChunkX - ((nextChunkX / 8) * 8));
				int regionOffsetY = (nextChunkY - ((nextChunkY / 8) * 8));
				for (int pIndex = 0; pIndex < toPlanes.length; pIndex++) {
					int toPlane = toPlanes[pIndex];
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = 0;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = 0;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = 0;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][3] = 0;
				}
			}
		}
	}

	/*
	 * copys a single 8x8 map tile and allows you to rotate it
	 */
	public static void copyChunk(int fromChunkX, int fromChunkY,
			int fromPlane, int toChunkX, int toChunkY, int toPlane,
			int rotation) {
		DynamicRegion toRegion = createDynamicRegion((((toChunkX / 8) << 8) + (toChunkY / 8)));
		int regionOffsetX = (toChunkX - ((toChunkX / 8) * 8));
		int regionOffsetY = (toChunkY - ((toChunkY / 8) * 8));
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = fromChunkX;
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = fromChunkY;
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = fromPlane;
		toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][3] = rotation;
		World.getRegion((((fromChunkY / 8) << 8) + (fromChunkX / 8)), true);
	}

	/*
	 * copy a exactly square of map from a place to another
	 */
	public static final void copyAllPlanesMap(int fromRegionX, int fromRegionY,
			int toRegionX, int toRegionY, int ratio) {
		int[] planes = new int[4];
		for (int plane = 1; plane < 4; plane++)
			planes[plane] = plane;
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, ratio, ratio,
				planes, planes);
	}

	/*
	 * copy a exactly square of map from a place to another
	 */
	public static final void copyAllPlanesMap(int fromRegionX, int fromRegionY,
			int toRegionX, int toRegionY, int widthRegions, int heightRegions) {
		int[] planes = new int[4];
		for (int plane = 1; plane < 4; plane++)
			planes[plane] = plane;
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, widthRegions,
				heightRegions, planes, planes);
	}

	/*
	 * copy a square of map from a place to another
	 */
	public static final void copyMap(int fromRegionX, int fromRegionY,
			int toRegionX, int toRegionY, int ratio, int[] fromPlanes,
			int[] toPlanes) {
		copyMap(fromRegionX, fromRegionY, toRegionX, toRegionY, ratio, ratio,
				fromPlanes, toPlanes);
	}

	/*
	 * copy a rectangle of map from a place to another
	 */
	public static final void copyMap(int fromRegionX, int fromRegionY,
			int toRegionX, int toRegionY, int widthRegions, int heightRegions,
			int[] fromPlanes, int[] toPlanes) {
		if (fromPlanes.length != toPlanes.length)
			throw new RuntimeException(
					"PLANES LENGTH ISNT SAME OF THE NEW PLANES ORDER!");
		for (int xOffset = 0; xOffset < widthRegions; xOffset++) {
			for (int yOffset = 0; yOffset < heightRegions; yOffset++) {
				int fromThisRegionX = fromRegionX + xOffset;
				int fromThisRegionY = fromRegionY + yOffset;
				int toThisRegionX = toRegionX + xOffset;
				int toThisRegionY = toRegionY + yOffset;
				int regionId = ((toThisRegionX / 8) << 8) + (toThisRegionY / 8);
				DynamicRegion toRegion = createDynamicRegion(regionId);
				int regionOffsetX = (toThisRegionX - ((toThisRegionX / 8) * 8));
				int regionOffsetY = (toThisRegionY - ((toThisRegionY / 8) * 8));
				for (int pIndex = 0; pIndex < fromPlanes.length; pIndex++) {
					int toPlane = toPlanes[pIndex];
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][0] = fromThisRegionX;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][1] = fromThisRegionY;
					toRegion.getRegionCoords()[toPlane][regionOffsetX][regionOffsetY][2] = fromPlanes[pIndex];
					World.getRegion((regionId), true);
				}
			}
		}
	}

	/*
	 * temporary and used for dungeonnering only
	 * 
	 * //rotation 0 // a b // c d //rotation 1 // c a // d b //rotation2 // d c
	 * // b a //rotation3 // b d // a c
	 */
	public static final void copy2RatioSquare(int fromRegionX, int fromRegionY,
			int toRegionX, int toRegionY, int rotation) {
		if (rotation == 0) {
			copyChunk(fromRegionX, fromRegionY, 0, toRegionX, toRegionY, 0,
					rotation);
			copyChunk(fromRegionX + 1, fromRegionY, 0, toRegionX + 1,
					toRegionY, 0, rotation);
			copyChunk(fromRegionX, fromRegionY + 1, 0, toRegionX,
					toRegionY + 1, 0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY + 1, 0, toRegionX + 1,
					toRegionY + 1, 0, rotation);
		} else if (rotation == 1) {
			copyChunk(fromRegionX, fromRegionY, 0, toRegionX, toRegionY + 1,
					0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY, 0, toRegionX, toRegionY,
					0, rotation);
			copyChunk(fromRegionX, fromRegionY + 1, 0, toRegionX + 1,
					toRegionY + 1, 0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY + 1, 0, toRegionX + 1,
					toRegionY, 0, rotation);
		} else if (rotation == 2) {
			copyChunk(fromRegionX, fromRegionY, 0, toRegionX + 1,
					toRegionY + 1, 0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY, 0, toRegionX,
					toRegionY + 1, 0, rotation);
			copyChunk(fromRegionX, fromRegionY + 1, 0, toRegionX + 1,
					toRegionY, 0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY + 1, 0, toRegionX,
					toRegionY, 0, rotation);
		} else if (rotation == 3) {
			copyChunk(fromRegionX, fromRegionY, 0, toRegionX + 1, toRegionY,
					0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY, 0, toRegionX + 1,
					toRegionY + 1, 0, rotation);
			copyChunk(fromRegionX, fromRegionY + 1, 0, toRegionX, toRegionY,
					0, rotation);
			copyChunk(fromRegionX + 1, fromRegionY + 1, 0, toRegionX,
					toRegionY + 1, 0, rotation);
		}
	}

	/*
	 * not recommended to use unless you want to make a more complex map
	 */
	public static DynamicRegion createDynamicRegion(int regionId) {
		synchronized (ALGORITHM_LOCK) {
			Region region = World.getRegions().get(regionId);
			if (region != null) {
				if (region instanceof DynamicRegion) // if its already dynamic lets
														// keep building it
					return (DynamicRegion) region;
				else
					destroyRegion(regionId);
			}
			DynamicRegion newRegion = new DynamicRegion(regionId);
			World.getRegions().put(regionId, newRegion);
			return newRegion;
		}
	}

	public static void spawnGroundItems() {
		World.addGroundItem(new Item(964, 1), new WorldTile(2977, 3527, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(2978, 3531, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(2988, 3676, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(2985, 3676, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(2978, 3674, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2983, 3697, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2978, 3704, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2963, 3706, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2955, 3702, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2955, 3700, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2958, 3697, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2957, 3697, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(2976, 3681, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(2970, 3705, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(2981, 3691, 0));
		World.addGroundItem(new Item(1539, 5), new WorldTile(2981, 3689, 0));
		World.addGroundItem(new Item(1539, 5), new WorldTile(2984, 3685, 0));
		World.addGroundItem(new Item(1539, 5), new WorldTile(2989, 3683, 0));
		World.addGroundItem(new Item(2347, 1), new WorldTile(2985, 3685, 0));
		World.addGroundItem(new Item(2347, 1), new WorldTile(2992, 3686, 0));
		World.addGroundItem(new Item(2347, 1), new WorldTile(2957, 3708, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2956, 3707, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2952, 3704, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2961, 3701, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2964, 3704, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2957, 3699, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2974, 3708, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2975, 3697, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2983, 3700, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(2968, 3683, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2961, 3697, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2969, 3689, 0));
		World.addGroundItem(new Item(5341, 1), new WorldTile(2999, 3702, 0));
		World.addGroundItem(new Item(5343, 1), new WorldTile(2997, 3703, 0));
		World.addGroundItem(new Item(1887, 1), new WorldTile(2986, 3686, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2979, 3763, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2980, 3764, 0));
		World.addGroundItem(new Item(564, 3), new WorldTile(2961, 3893, 0));
		World.addGroundItem(new Item(555, 3), new WorldTile(2961, 3893, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3002, 3923, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3004, 3919, 0));
		World.addGroundItem(new Item(1985, 1), new WorldTile(3039, 3707, 0));
		World.addGroundItem(new Item(1982, 1), new WorldTile(3039, 3706, 0));
		World.addGroundItem(new Item(1654, 1), new WorldTile(3067, 3865, 0));
		World.addGroundItem(new Item(2333, 1), new WorldTile(3042, 3952, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3100, 3596, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3097, 3595, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3096, 3598, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3102, 3597, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3101, 3594, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3104, 3609, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3108, 3611, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3113, 3606, 0));
		World.addGroundItem(new Item(1153, 1), new WorldTile(3077, 3826, 0));
		World.addGroundItem(new Item(1119, 1), new WorldTile(3085, 3857, 0));
		World.addGroundItem(new Item(1385, 1), new WorldTile(3100, 3860, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(3106, 3952, 0));
		World.addGroundItem(new Item(562, 3), new WorldTile(3100, 3860, 0));
		World.addGroundItem(new Item(562, 3), new WorldTile(3145, 3825, 0));
		World.addGroundItem(new Item(562, 3), new WorldTile(3148, 3829, 0));
		World.addGroundItem(new Item(562, 3), new WorldTile(3147, 3821, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3235, 3556, 0));
		World.addGroundItem(new Item(559, 10), new WorldTile(3223, 3567, 0));
		World.addGroundItem(new Item(559, 10), new WorldTile(3231, 3573, 0));
		World.addGroundItem(new Item(559, 10), new WorldTile(3225, 3580, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3239, 3603, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3238, 3603, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3237, 3603, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3236, 3604, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3237, 3608, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3236, 3609, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3243, 3612, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3247, 3612, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3239, 3602, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3238, 3602, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3237, 3602, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3236, 3602, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3210, 3679, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3216, 3678, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3219, 3680, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3217, 3666, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3224, 3669, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3233, 3688, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3244, 3679, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(3245, 3681, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(3236, 3695, 0));
		World.addGroundItem(new Item(837, 1), new WorldTile(3236, 3670, 0));
		World.addGroundItem(new Item(1137, 1), new WorldTile(3239, 3691, 0));
		World.addGroundItem(new Item(1207, 1), new WorldTile(3211, 3682, 0));
		World.addGroundItem(new Item(1203, 1), new WorldTile(3213, 3694, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3246, 3716, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3254, 3731, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3256, 3731, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3255, 3741, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3247, 3748, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3240, 3747, 0));
		World.addGroundItem(new Item(964, 1), new WorldTile(3235, 3747, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3227, 3748, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3241, 3719, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3246, 3724, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3249, 3729, 0));
		World.addGroundItem(new Item(444, 1), new WorldTile(3230, 3740, 0));
		World.addGroundItem(new Item(444, 1), new WorldTile(3237, 3738, 0));
		World.addGroundItem(new Item(590, 1), new WorldTile(3209, 3735, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3178, 3849, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3177, 3851, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3180, 3848, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3185, 3849, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3187, 3853, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3184, 3853, 0));
		World.addGroundItem(new Item(239, 1), new WorldTile(3215, 3808, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3219, 3811, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3220, 3813, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3217, 3814, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3214, 3817, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3212, 3819, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3220, 3820, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3219, 3823, 0));
		World.addGroundItem(new Item(1217, 1), new WorldTile(3180, 3822, 0));
		World.addGroundItem(new Item(229, 1), new WorldTile(3187, 3836, 0));
		World.addGroundItem(new Item(229, 1), new WorldTile(3197, 3845, 0));
		World.addGroundItem(new Item(1191, 1), new WorldTile(3247, 3793, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3232, 3945, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3236, 3947, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3246, 3949, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3240, 3940, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3243, 3942, 0));
		World.addGroundItem(new Item(53, 1), new WorldTile(3251, 3940, 0));
		World.addGroundItem(new Item(53, 1), new WorldTile(3255, 3942, 0));
		World.addGroundItem(new Item(1607, 1), new WorldTile(3170, 3886, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3294, 3650, 0));
		World.addGroundItem(new Item(966, 1), new WorldTile(3294, 3647, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3272, 3655, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3272, 3658, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3270, 3658, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(3280, 3657, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3276, 3662, 0));
		World.addGroundItem(new Item(960, 1), new WorldTile(3274, 3662, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3287, 3884, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3288, 3888, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3290, 3888, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3291, 3887, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3292, 3886, 0));
		World.addGroundItem(new Item(565, 1), new WorldTile(3295, 3888, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3311, 3885, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3313, 3883, 0));
		World.addGroundItem(new Item(528, 1), new WorldTile(3321, 3890, 0));
		World.addGroundItem(new Item(561, 1), new WorldTile(3309, 3859, 0));
		World.addGroundItem(new Item(561, 1), new WorldTile(3312, 3853, 0));
		World.addGroundItem(new Item(1203, 1), new WorldTile(3281, 3938, 0));
		World.addGroundItem(new Item(444, 1), new WorldTile(3283, 3933, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2740, 3637, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2736, 3638, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2735, 3636, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2743, 3636, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2739, 3634, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2743, 3640, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2741, 3639, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2736, 3641, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2738, 3641, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2734, 3640, 0));
		World.addGroundItem(new Item(1573, 1), new WorldTile(2738, 3636, 0));
		World.addGroundItem(new Item(401, 1), new WorldTile(2764, 3131, 0));
		World.addGroundItem(new Item(401, 1), new WorldTile(2753, 3125, 0));
		World.addGroundItem(new Item(401, 1), new WorldTile(2757, 3109, 0));
		World.addGroundItem(new Item(1351, 1), new WorldTile(2795, 3161, 0));
		World.addGroundItem(new Item(1917, 1), new WorldTile(2799, 3156, 0));
		World.addGroundItem(new Item(1917, 1), new WorldTile(2796, 3165, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2798, 3156, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2799, 3155, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2795, 3160, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2798, 3160, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2798, 3161, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2795, 3165, 0));
		World.addGroundItem(new Item(1919, 1), new WorldTile(2794, 3165, 0));
		World.addGroundItem(new Item(28, 1), new WorldTile(2807, 3450, 0));
		World.addGroundItem(new Item(401, 1), new WorldTile(2864, 3195, 0));
		World.addGroundItem(new Item(357, 1), new WorldTile(2820, 3453, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(2820, 3450, 0));
		World.addGroundItem(new Item(1929, 1), new WorldTile(2820, 3452, 0));
		World.addGroundItem(new Item(1929, 1), new WorldTile(2823, 3449, 0));
		World.addGroundItem(new Item(2313, 1), new WorldTile(2820, 3455, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2831, 9766, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2829, 9764, 0));
		World.addGroundItem(new Item(556, 1), new WorldTile(2938, 3158, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(2903, 3148, 0));
		World.addGroundItem(new Item(1935, 1), new WorldTile(2905, 3146, 0));
		World.addGroundItem(new Item(1963, 1), new WorldTile(2907, 3146, 0));
		World.addGroundItem(new Item(231, 1), new WorldTile(2905, 3297, 0));
		World.addGroundItem(new Item(231, 1), new WorldTile(2907, 3295, 0));
		World.addGroundItem(new Item(1592, 1), new WorldTile(2935, 3283, 1));
		World.addGroundItem(new Item(1595, 1), new WorldTile(2928, 3290, 0));
		World.addGroundItem(new Item(1597, 1), new WorldTile(2932, 3287, 1));
		World.addGroundItem(new Item(1599, 1), new WorldTile(2931, 3287, 1));
		World.addGroundItem(new Item(1735, 1), new WorldTile(2930, 3285, 1));
		World.addGroundItem(new Item(1735, 1), new WorldTile(2935, 3286, 0));
		World.addGroundItem(new Item(1935, 1), new WorldTile(2936, 3292, 0));
		World.addGroundItem(new Item(2347, 1), new WorldTile(2934, 3286, 0));
		World.addGroundItem(new Item(5523, 1), new WorldTile(2935, 3282, 1));
		World.addGroundItem(new Item(11065, 1), new WorldTile(2928, 3289, 0));
		World.addGroundItem(new Item(712, 1), new WorldTile(2887, 3412, 0));
		World.addGroundItem(new Item(712, 1), new WorldTile(2903, 3441, 0));
		World.addGroundItem(new Item(2150, 1), new WorldTile(2907, 3393, 0));
		World.addGroundItem(new Item(2150, 1), new WorldTile(2903, 3400, 0));
		World.addGroundItem(new Item(2150, 1), new WorldTile(2908, 3410, 0));
		World.addGroundItem(new Item(2162, 1), new WorldTile(2896, 3414, 0));
		World.addGroundItem(new Item(245, 1), new WorldTile(2931, 3515, 0));
		World.addGroundItem(new Item(3109, 1), new WorldTile(2893, 3565, 0));
		World.addGroundItem(new Item(3110, 1), new WorldTile(2893, 3564, 0));
		World.addGroundItem(new Item(3111, 1), new WorldTile(2893, 3561, 0));
		World.addGroundItem(new Item(3112, 1), new WorldTile(2893, 3563, 0));
		World.addGroundItem(new Item(3113, 1), new WorldTile(2893, 3562, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2912, 9809, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2914, 9802, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2912, 9800, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2908, 9807, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2911, 9804, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2904, 9798, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2905, 9804, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2901, 9806, 0));
		World.addGroundItem(new Item(243, 1), new WorldTile(2909, 9799, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2933, 9834, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2922, 9820, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2914, 9849, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2917, 9850, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2910, 9826, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2907, 9824, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2906, 9823, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2906, 9825, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2903, 9826, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2924, 9801, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2926, 9801, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2924, 9804, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2929, 9807, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2936, 9799, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2936, 9799, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2931, 9792, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2938, 9792, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(2944, 3332, 0));
		World.addGroundItem(new Item(952, 1), new WorldTile(2981, 3370, 0));
		World.addGroundItem(new Item(1351, 1), new WorldTile(2970, 3376, 1));
		World.addGroundItem(new Item(2140, 1), new WorldTile(2971, 3382, 1));
		World.addGroundItem(new Item(2347, 1), new WorldTile(2975, 3368, 1));
		World.addGroundItem(new Item(1925, 1), new WorldTile(2958, 3510, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(2958, 3510, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2966, 9772, 0));
		World.addGroundItem(new Item(526, 1), new WorldTile(2968, 9771, 0));
		World.addGroundItem(new Item(1005, 1), new WorldTile(3014, 3227, 0));
		World.addGroundItem(new Item(1005, 1), new WorldTile(3009, 3204, 0));
		World.addGroundItem(new Item(1963, 1), new WorldTile(3009, 3207, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3026, 3289, 0));
		World.addGroundItem(new Item(1944, 1), new WorldTile(3015, 3295, 0));
		World.addGroundItem(new Item(1944, 1), new WorldTile(3016, 3295, 0));
		World.addGroundItem(new Item(542, 1), new WorldTile(3059, 3488, 1));
		World.addGroundItem(new Item(544, 1), new WorldTile(3059, 3487, 1));
		World.addGroundItem(new Item(1061, 1), new WorldTile(3111, 3159, 0));
		World.addGroundItem(new Item(1061, 1), new WorldTile(3112, 3155, 0));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3106, 3160, 0));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3106, 3159, 0));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3105, 3159, 0));
		World.addGroundItem(new Item(1982, 1), new WorldTile(3084, 3258, 1));
		World.addGroundItem(new Item(1985, 1), new WorldTile(3081, 3261, 1));
		World.addGroundItem(new Item(272, 1), new WorldTile(3108, 3356, 1));
		World.addGroundItem(new Item(273, 1), new WorldTile(3097, 3366, 0));
		World.addGroundItem(new Item(276, 1), new WorldTile(3111, 3367, 0));
		World.addGroundItem(new Item(590, 1), new WorldTile(3112, 3369, 2));
		World.addGroundItem(new Item(952, 1), new WorldTile(3120, 3359, 0));
		World.addGroundItem(new Item(1139, 1), new WorldTile(3122, 3360, 0));
		World.addGroundItem(new Item(1735, 1), new WorldTile(3126, 3356, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3121, 3359, 0));
		World.addGroundItem(new Item(1265, 1), new WorldTile(3081, 3429, 0));
		World.addGroundItem(new Item(1917, 1), new WorldTile(3080, 3438, 0));
		World.addGroundItem(new Item(1917, 1), new WorldTile(3077, 3439, 0));
		World.addGroundItem(new Item(1917, 1), new WorldTile(3077, 3443, 0));
		World.addGroundItem(new Item(2142, 1), new WorldTile(3080, 3443, 0));
		World.addGroundItem(new Item(2142, 1), new WorldTile(3077, 3441, 0));
		World.addGroundItem(new Item(1059, 1), new WorldTile(3097, 3486, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3102, 3563, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3103, 3554, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3105, 3547, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3107, 3533, 0));
		World.addGroundItem(new Item(995, 5), new WorldTile(3104, 3577, 0));
		World.addGroundItem(new Item(983, 1), new WorldTile(3131, 9862, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3128, 9956, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3129, 9954, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3126, 9958, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3117, 9951, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3118, 9948, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3119, 9949, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3193, 3181, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3194, 3168, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3191, 3162, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3189, 3163, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3185, 3161, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3182, 3165, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3172, 3166, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3170, 3167, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3164, 3169, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3171, 3177, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3173, 3178, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3164, 3180, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3165, 3187, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3171, 3191, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3178, 3190, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3181, 3193, 0));
		World.addGroundItem(new Item(1939, 1), new WorldTile(3182, 3181, 0));
		World.addGroundItem(new Item(1735, 1), new WorldTile(3192, 3272, 0));
		World.addGroundItem(new Item(1735, 1), new WorldTile(3152, 3306, 0));
		World.addGroundItem(new Item(1931, 1), new WorldTile(3166, 3310, 0));
		World.addGroundItem(new Item(15412, 1), new WorldTile(3191, 3276, 0));
		World.addGroundItem(new Item(1887, 1), new WorldTile(3141, 3452, 1));
		World.addGroundItem(new Item(1923, 1), new WorldTile(3140, 3452, 1));
		World.addGroundItem(new Item(1931, 1), new WorldTile(3144, 3449, 2));
		World.addGroundItem(new Item(1935, 1), new WorldTile(3142, 3447, 2));
		World.addGroundItem(new Item(1955, 1), new WorldTile(3141, 3447, 1));
		World.addGroundItem(new Item(1955, 1), new WorldTile(3140, 3447, 1));
		World.addGroundItem(new Item(1955, 1), new WorldTile(3141, 3447, 2));
		World.addGroundItem(new Item(1973, 1), new WorldTile(3143, 3453, 0));
		World.addGroundItem(new Item(1987, 1), new WorldTile(3144, 3450, 2));
		World.addGroundItem(new Item(2313, 1), new WorldTile(3142, 3447, 1));
		World.addGroundItem(new Item(223, 1), new WorldTile(3179, 9881, 0));
		World.addGroundItem(new Item(223, 1), new WorldTile(3177, 9880, 0));
		World.addGroundItem(new Item(303, 1), new WorldTile(3245, 3155, 0));
		World.addGroundItem(new Item(303, 1), new WorldTile(3244, 3159, 0));
		World.addGroundItem(new Item(558, 1), new WorldTile(3206, 3208, 0));
		World.addGroundItem(new Item(882, 1), new WorldTile(3205, 3227, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(3205, 3212, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(3224, 3202, 0));
		World.addGroundItem(new Item(1203, 1), new WorldTile(3248, 3245, 0));
		World.addGroundItem(new Item(1205, 1), new WorldTile(3213, 3216, 1));
		World.addGroundItem(new Item(1265, 1), new WorldTile(3229, 3223, 2));
		World.addGroundItem(new Item(1265, 1), new WorldTile(3229, 3215, 2));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3205, 3226, 2));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3205, 3224, 2));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3208, 3225, 2));
		World.addGroundItem(new Item(1511, 1), new WorldTile(3209, 3224, 2));
		World.addGroundItem(new Item(1923, 1), new WorldTile(3208, 3214, 0));
		World.addGroundItem(new Item(1931, 1), new WorldTile(3209, 3214, 0));
		World.addGroundItem(new Item(1935, 1), new WorldTile(3211, 3212, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3225, 3294, 0));
		World.addGroundItem(new Item(1944, 1), new WorldTile(3229, 3299, 0));
		World.addGroundItem(new Item(1944, 1), new WorldTile(3226, 3301, 0));
		World.addGroundItem(new Item(15412, 1), new WorldTile(3231, 3290, 0));
		World.addGroundItem(new Item(767, 1), new WorldTile(3245, 3385, 1));
		World.addGroundItem(new Item(767, 1), new WorldTile(3243, 3383, 1));
		World.addGroundItem(new Item(1059, 1), new WorldTile(3242, 3385, 1));
		World.addGroundItem(new Item(1061, 1), new WorldTile(3244, 3386, 1));
		World.addGroundItem(new Item(1203, 1), new WorldTile(3242, 3383, 1));
		World.addGroundItem(new Item(33, 1), new WorldTile(3208, 3395, 1));
		World.addGroundItem(new Item(946, 1), new WorldTile(3218, 3416, 1));
		World.addGroundItem(new Item(952, 1), new WorldTile(3218, 3412, 1));
		World.addGroundItem(new Item(1171, 1), new WorldTile(3217, 3514, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3221, 3497, 1));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3222, 3491, 1));
		World.addGroundItem(new Item(2313, 1), new WorldTile(3222, 3494, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3216, 9625, 0));
		World.addGroundItem(new Item(1965, 1), new WorldTile(3217, 9622, 0));
		World.addGroundItem(new Item(1061, 1), new WorldTile(3210, 9615, 0));
		World.addGroundItem(new Item(1061, 1), new WorldTile(3208, 9620, 0));
		World.addGroundItem(new Item(1935, 1), new WorldTile(3211, 9625, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(3215, 9625, 0));
		World.addGroundItem(new Item(946, 1), new WorldTile(3218, 9887, 0));
		World.addGroundItem(new Item(1061, 1), new WorldTile(3301, 3191, 0));
		World.addGroundItem(new Item(1925, 1), new WorldTile(3307, 3195, 0));
		World.addGroundItem(new Item(1937, 1), new WorldTile(3302, 3170, 0));
		World.addGroundItem(new Item(1965, 1), new WorldTile(3285, 3175, 0));
		World.addGroundItem(new Item(1422, 1), new WorldTile(3320, 3137, 0));
		World.addGroundItem(new Item(559, 1), new WorldTile(3290, 3191, 1));
		World.addGroundItem(new Item(4199, 1), new WorldTile(3492, 3474, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2280, 4707, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2284, 4701, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2265, 4684, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2261, 4689, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2260, 4697, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2261, 4702, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2270, 4689, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2273, 4687, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2282, 4686, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2278, 4691, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2282, 4697, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2271, 4697, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2274, 4698, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2276, 4700, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2274, 4703, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2270, 4703, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2267, 4699, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2265, 4694, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2283, 4703, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2280, 4707, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2267, 4707, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2260, 4687, 0));
		World.addGroundItem(new Item(995, 1), new WorldTile(2266, 4686, 0));

	}

	/*
	 * Safely destroys a dynamic region
	 */
	public static void destroyRegion(int regionId) {
			Region region = World.getRegions().get(regionId);
			if (region != null) {
				List<Integer> playerIndexes = region.getPlayerIndexes();
				List<Integer> npcIndexes = region.getNPCsIndexes();
				if (region.getFloorItems() != null) 
					region.getFloorItems().clear();
				if (region.getSpawnedObjects() != null) 
					region.getSpawnedObjects().clear();
				if (region.getRemovedObjects() != null) 
					region.getRemovedObjects().clear();
				if (npcIndexes != null) {
					for (int npcIndex : npcIndexes) {
						NPC npc = World.getNPCs().get(npcIndex);
						if (npc == null)
							continue;
						npc.finish();
					}
				}
				World.getRegions().remove(regionId);

				if (playerIndexes != null) {
					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player == null || !player.hasStarted()
								|| player.hasFinished())
							continue;
						player.setForceNextMapLoadRefresh(true);
						player.loadMapRegions();
					}
				}
			}
	}

	private RegionBuilder() {

	}
}
