package me.hyfe.helper.menu.scheme;

import com.google.common.collect.ImmutableList;
import me.hyfe.helper.menu.gui.Gui;
import me.hyfe.helper.menu.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MenuScheme {
    private static final boolean[] EMPTY_MASK = new boolean[]{false, false, false, false, false, false, false, false, false};
    private static final int[] EMPTY_SCHEME = new int[0];

    private final SchemeMapping mapping;
    private final List<boolean[]> maskRows;
    private final List<int[]> schemeRows;

    public MenuScheme() {
        this.mapping = new EmptySchemeMapping();
        this.maskRows = new ArrayList<>();
        this.schemeRows = new ArrayList<>();
    }

    private MenuScheme(MenuScheme other) {
        this.mapping = other.mapping.copy();
        this.maskRows = new ArrayList<>();
        for (boolean[] arr : other.maskRows) {
            this.maskRows.add(Arrays.copyOf(arr, arr.length));
        }
        this.schemeRows = new ArrayList<>();
        for (int[] arr : other.schemeRows) {
            this.schemeRows.add(Arrays.copyOf(arr, arr.length));
        }
    }

    public MenuScheme mask(String s) {
        char[] chars = s.replace(" ", "").toCharArray();
        if (chars.length != 9) {
            throw new IllegalArgumentException("invalid mask: " + s);
        }
        boolean[] ret = new boolean[9];
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '1' || c == 't') {
                ret[i] = true;
            } else if (c == '0' || c == 'f' || c == 'x') {
                ret[i] = false;
            } else {
                throw new IllegalArgumentException("invalid mask character: " + c);
            }
        }
        this.maskRows.add(ret);
        return this;
    }

    public MenuScheme masks(String... strings) {
        for (String s : strings) {
            mask(s);
        }
        return this;
    }

    public MenuScheme maskEmpty(int lines) {
        for (int i = 0; i < lines; i++) {
            this.maskRows.add(EMPTY_MASK);
            this.schemeRows.add(EMPTY_SCHEME);
        }
        return this;
    }

    public MenuScheme scheme(int... schemeIds) {
        for (int schemeId : schemeIds) {
            if (!this.mapping.hasMappingFor(schemeId)) {
                throw new IllegalArgumentException("mapping does not contain value for id: " + schemeId);
            }
        }
        this.schemeRows.add(schemeIds);
        return this;
    }

    public void apply(Gui gui) {
        int invIndex = 0;
        for (int i = 0; i < this.maskRows.size(); i++) {
            boolean[] mask = this.maskRows.get(i);
            int[] scheme = this.schemeRows.get(i);
            int schemeIndex = 0;
            for (boolean isMasked : mask) {
                int index = invIndex++;
                if (isMasked) {
                    int schemeMappingId = scheme[schemeIndex++];
                    Item item = this.mapping.get(schemeMappingId);
                    gui.setItem(item, index);
                }
            }
        }
    }

    public List<Integer> getMaskedIndexes() {
        List<Integer> ret = new LinkedList<>();
        int invIndex = 0;
        for (boolean[] mask : this.maskRows) {
            for (boolean isMasked : mask) {
                int index = invIndex++;
                if (isMasked) {
                    ret.add(index);
                }
            }
        }
        return ret;
    }

    public ImmutableList<Integer> getMaskedIndexesImmutable() {
        return ImmutableList.copyOf(getMaskedIndexes());
    }

    public MenuPopulator newPopulator(Gui gui) {
        return new MenuPopulator(gui, this);
    }

    public MenuScheme copy() {
        return new MenuScheme(this);
    }
}