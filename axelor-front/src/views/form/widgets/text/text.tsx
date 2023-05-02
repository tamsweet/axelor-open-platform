import React, { useCallback, useState } from "react";
import { useAtom, useAtomValue } from "jotai";
import { Box, Input } from "@axelor/ui";
import { FieldContainer, FieldProps } from "../../builder";
import classes from "./text.module.scss";

export function Text({
  schema,
  readonly,
  widgetAtom,
  valueAtom,
  inputProps,
}: FieldProps<string> & {
  inputProps?: Pick<
    React.InputHTMLAttributes<HTMLTextAreaElement>,
    "onFocus" | "onBlur" | "autoFocus"
  >;
}) {
  const { uid, height, placeholder, showTitle = true } = schema;
  const { onBlur } = inputProps || {};

  const { attrs } = useAtomValue(widgetAtom);
  const { title, required } = attrs;

  const [value, setValue] = useAtom(valueAtom);
  const [changed, setChanged] = useState(false);

  const handleChange = useCallback<
    React.ChangeEventHandler<HTMLTextAreaElement>
  >(
    (e) => {
      setValue(e.target.value);
      setChanged(true);
    },
    [setValue]
  );

  const handleBlur = useCallback<React.FocusEventHandler<HTMLTextAreaElement>>(
    (e) => {
      if (changed) {
        setChanged(false);
        setValue(e.target.value, true);
      }
      onBlur?.(e);
    },
    [changed, setValue, onBlur]
  );

  return (
    <FieldContainer readonly={readonly}>
      {showTitle && <label htmlFor={uid}>{title}</label>}
      {readonly ? (
        <Box
          p={1}
          w={100}
          className={classes.content}
          dangerouslySetInnerHTML={{ __html: value ?? "" }}
          {...(value ? { as: "pre" } : {})}
        />
      ) : (
        <Input
          data-input
          as="textarea"
          rows={height || 5}
          id={uid}
          placeholder={placeholder}
          value={value || ""}
          required={required}
          {...inputProps}
          onChange={handleChange}
          onBlur={handleBlur}
        />
      )}
    </FieldContainer>
  );
}
