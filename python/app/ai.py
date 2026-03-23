from openai import OpenAI
from pathlib import Path
from dotenv import load_dotenv
import os

CLIENT = None

def load_environment():
    """Load environment variables from .env file."""
    env_path = Path(__file__).parent / '.env'
    if env_path.exists():
        print("Loading API key from file")
        load_dotenv(env_path)
    api_key = os.getenv('OPENAI_API_KEY')
    
    if not api_key or api_key == 'your_openai_api_key_here':
        raise ValueError("Please set a valid OPENAI_API_KEY in the .env file")
    
    return api_key

def get_client() -> OpenAI:
    global CLIENT
    if not CLIENT:
        CLIENT = OpenAI(api_key=load_environment())
    return CLIENT