import re
import pandas as pd

def get_stack_name(file_path: str) -> str:
    data = pd.read_csv(file_path)
    return data['stack_name'].dropna().unique()

def format_enum_entry(name):
    name = re.sub(r"\(.*?\)", "", name)

    if name[0].isdigit():
        name = "_" + name

    return (
        name
        .upper()
        .replace("/", "_")
        .replace(" ", "_")
        .replace("-", "_")
        .replace("'", "")
        .replace("&", "AND")
        .replace(".", "DOT")
        .replace("++", "PP")
        .replace("#", "SHARP")
    )

def generate_java_enum(file_path, package_name, class_name):
    unique_stack_names = get_stack_name(file_path)

    formatted_enum_entries = [
        f'{format_enum_entry(name)}("{name}")' for name in unique_stack_names
    ]

    enum_body = ",\n\t".join(formatted_enum_entries)

    java_enum = f"""package {package_name};

public enum {class_name} {{
    {enum_body};

    private final String displayName;

    {class_name}(String displayName) {{
        this.displayName = displayName;
    }}

    public String getDisplayName() {{
        return displayName;
    }}
}}"""

    return java_enum

def generate_graphql_enum(file_path: str, type_name: str) -> str:
    unique_stack_names = get_stack_name(file_path)

    formatted_enum_entries = [
        f'{format_enum_entry(name)}' for name in unique_stack_names
    ]

    enum_body = "\n\t".join(formatted_enum_entries)

    graphql_enum = f"""enum {type_name} {{
    {enum_body}
}}"""

    return graphql_enum

if __name__ == "__main__":
    file_path = 'stack_dataset.csv'

    class_name = "ProjectStack"
    package_name = "org.pofo.domain.rds.domain.project"
    java_enum_code = generate_java_enum(file_path, package_name, class_name)

    output_file_path = f'{class_name}.java'
    with open(output_file_path, 'w', encoding='utf-8') as file:
        file.write(java_enum_code)
    print(f"Java enum class saved to {output_file_path}")

    type_name = "ProjectStack"
    graphql_enum_code = generate_graphql_enum(file_path, type_name)

    output_file_path = f'project.stack.graphqls'
    with open(output_file_path, 'w', encoding='utf-8') as file:
        file.write(graphql_enum_code)
    print(f"graphql enum type saved to {output_file_path}")
