import { PrimitiveAtom, useAtom } from "jotai";
import { focusAtom } from "jotai-optics";

import { Box, Input, Select } from "@axelor/ui";
import { GridColumn } from "@axelor/ui/grid";

import { i18n } from "@/services/client/i18n";
import { Field } from "@/services/client/meta.types";
import {
  ChangeEvent,
  KeyboardEvent,
  SyntheticEvent,
  useMemo,
  useState,
} from "react";

import { SearchState } from "./types";

import styles from "./search-column.module.scss";

export interface SearchColumnProps {
  column: GridColumn;
  dataAtom: PrimitiveAtom<SearchState>;
  onSearch?: () => void;
}

function SearchInput({ column, dataAtom, onSearch }: SearchColumnProps) {
  const field = column as Field;
  const [value, setValue] = useAtom(
    useMemo(
      () => focusAtom(dataAtom, (o) => o.prop(column.name).valueOr("")),
      [column.name, dataAtom]
    )
  );
  const [focus, setFocus] = useState(false);

  function applySearch() {
    onSearch?.();
  }

  function handleChange(e: ChangeEvent<HTMLInputElement>) {
    setValue(e.target.value);
  }

  function handleFocus() {
    setFocus(true);
  }

  function handleBlur(e: ChangeEvent<HTMLInputElement>) {
    setFocus(false);
  }

  function handleKeyDown(e: KeyboardEvent<HTMLInputElement>) {
    if (
      [
        "Enter",
        "Tab",
        "ArrowRight",
        "ArrowLeft",
        "ArrowUp",
        "ArrowDown",
      ].includes(e.key)
    ) {
      e.stopPropagation();
    }
    if (e.key === "Enter") {
      e.preventDefault();
      applySearch();
    }
  }

  if (field.selectionList) {
    const selected = field.selectionList.find((opt) => opt.value === value);
    return (
      <Box w={100} className={styles.select} d="flex">
        <Select
          value={selected ?? null}
          placeholder={focus ? i18n.get("Search...") : ""}
          onChange={(e) => {
            const value = e?.value ?? null;
            setValue(value);
            applySearch();
          }}
          options={field.selectionList}
          optionLabel="title"
          optionValue="value"
          onFocus={handleFocus}
          onBlur={() => setFocus(false)}
          icons={[
            {
              icon: "close",
              hidden: !selected,
              onClick: () => {
                setValue("");
                applySearch();
              },
            },
            { icon: "arrow_drop_down" },
          ]}
          onKeyDown={handleKeyDown as (e: SyntheticEvent) => void}
        />
      </Box>
    );
  }

  return (
    <Box className={styles.container} d="flex">
      <Input
        type="text"
        value={value || ""}
        onChange={handleChange}
        placeholder={focus ? i18n.get("Search...") : ""}
        onKeyDown={handleKeyDown}
        onFocus={handleFocus}
        onBlur={handleBlur}
      />
    </Box>
  );
}

export function SearchColumn(props: SearchColumnProps) {
  const { column } = props;
  if (column.searchable === false) {
    return <Box h={100} w={100} />;
  }

  return <SearchInput {...props} />;
}