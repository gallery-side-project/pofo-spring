import re
import pandas as pd

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
    # Load the CSV file
    data = pd.read_csv(file_path)

    # Extract unique stack names
    unique_stack_names = data['stack_name'].dropna().unique()

    # Format the stack names for Java enum
    formatted_enum_entries = [
        f'{format_enum_entry(name)}("{name}")' for name in unique_stack_names
    ]

    # Join all entries with commas for the enum definition
    enum_body = ",\n\t".join(formatted_enum_entries)

    # Create the full enum class
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

if __name__ == "__main__":
    class_name = "ProjectStack"
    package_name = "org.pofo.domain.rds.domain.project"
    file_path = 'stack_dataset.csv'

    java_enum_code = generate_java_enum(file_path, package_name, class_name)

    # Save the generated Java enum code to a file
    output_file_path = f'{class_name}.java'  # 원하는 파일 이름과 경로
    with open(output_file_path, 'w', encoding='utf-8') as file:
        file.write(java_enum_code)

    print(f"Java enum class saved to {output_file_path}")

