import { useAtomValue } from "jotai";
import { focusAtom } from "jotai-optics";
import { useMemo } from "react";

import { createWidgetAtom } from "./atoms";
import { useWidgetComp } from "./hooks";
import { WidgetProps } from "./types";

export function FormWidget(props: Omit<WidgetProps, "widgetAtom">) {
  const { schema, formAtom, readonly } = props;

  const widgetAtom = useMemo(
    () => createWidgetAtom({ schema, formAtom }),
    [formAtom, schema]
  );

  const { attrs } = useAtomValue(widgetAtom);

  const widget = schema.widget!;
  const type = schema.type;

  const { state, data: Comp } = useWidgetComp(widget);

  if (state === "loading") {
    return <div>Loading...</div>;
  }

  const widgetProps = {
    schema,
    formAtom,
    widgetAtom,
    readonly: readonly ?? attrs.readonly,
  };

  if (Comp) {
    return type === "field" ? (
      <FormField component={Comp} {...widgetProps} />
    ) : (
      <Comp {...widgetProps} />
    );
  }
  return <Unknown {...widgetProps} />;
}

function FormField(props: WidgetProps & { component: React.ElementType }) {
  const { schema, formAtom, component: Comp } = props;
  const name = schema.name!;

  const valueAtom = useMemo(
    () => focusAtom(formAtom, (o) => o.prop("record").prop(name)),
    [formAtom, name]
  );

  return <Comp {...props} valueAtom={valueAtom} />;
}

function Unknown(props: WidgetProps) {
  const { schema } = props;
  return <div>{schema.widget}</div>;
}
