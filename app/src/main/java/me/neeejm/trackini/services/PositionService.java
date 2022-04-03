package me.neeejm.trackini.services;

import java.util.ArrayList;
import java.util.List;

import me.neeejm.trackini.beans.Position;
import me.neeejm.trackini.dao.IDao;

public class PositionService implements IDao<Position> {
    private List<Position> positions;
    private static PositionService instance;

    private PositionService() {
        this.positions =new ArrayList<>();
    }

    public static PositionService getInstance() {
        if (instance != null)
            instance = new PositionService();
        return instance;
    }

    @Override
    public boolean create(Position o) {
        return positions.add(o);
    }

    @Override
    public boolean delete(Position o) {
        return positions.remove(o);
    }

    @Override
    public List<Position> findAll() {
        return positions;
    }
}
