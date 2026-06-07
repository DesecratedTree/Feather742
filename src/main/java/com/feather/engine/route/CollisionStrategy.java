package com.feather.engine.route;

public interface CollisionStrategy {
    boolean canMove(int flag, int shape);

    boolean canWall(int destFlag, int currFlag, int dirFlag);

    CollisionStrategy NORMAL = new CollisionStrategy() {
        @Override
        public boolean canMove(int flag, int shape) {
            return !CollisionFlag.blocked(flag, shape);
        }

        @Override
        public boolean canWall(int destFlag, int currFlag, int dirFlag) {
            return !CollisionFlag.blockedWall(destFlag, currFlag, dirFlag);
        }
    };
}
